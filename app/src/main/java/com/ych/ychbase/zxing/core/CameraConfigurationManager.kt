package com.ych.ychbase.zxing.core

import android.content.Context
import android.graphics.Point
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Build
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.ych.ychbase.zxing.core.BGAQRCodeUtil.getOrientation
import com.ych.ychbase.zxing.core.BGAQRCodeUtil.getScreenResolution
import java.util.regex.Pattern
import kotlin.math.abs

internal class CameraConfigurationManager(private val mContext: Context) {
    private var mScreenResolution: Point? = null
    var cameraResolution: Point? = null
        private set
    private var mPreviewResolution: Point? = null
    fun initFromCameraParameters(camera: Camera) {
        val parameters = camera.parameters
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            if (autoFocusAble(camera)) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            }
        }
        mScreenResolution = getScreenResolution(mContext)
        val screenResolutionForCamera = Point()
        screenResolutionForCamera.x = mScreenResolution!!.x
        screenResolutionForCamera.y = mScreenResolution!!.y

        // preview size is always something like 480*320, other 320*480
        val orientation = getOrientation(mContext)
        if (orientation == BGAQRCodeUtil.ORIENTATION_PORTRAIT) {
            screenResolutionForCamera.x = mScreenResolution!!.y
            screenResolutionForCamera.y = mScreenResolution!!.x
        }
        mPreviewResolution = getPreviewResolution(
            parameters,
            screenResolutionForCamera
        )
        if (orientation == BGAQRCodeUtil.ORIENTATION_PORTRAIT) {
            cameraResolution = Point(mPreviewResolution!!.y, mPreviewResolution!!.x)
        } else {
            cameraResolution = mPreviewResolution
        }
    }

    fun setDesiredCameraParameters(camera: Camera) {
        val parameters = camera.parameters
        parameters.setPreviewSize(mPreviewResolution!!.x, mPreviewResolution!!.y)
        setZoom(parameters)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            camera.setDisplayOrientation(displayOrientation)
        }
        camera.parameters = parameters
    }

    fun openFlashlight(camera: Camera) {
        doSetTorch(camera, true)
    }

    fun closeFlashlight(camera: Camera) {
        doSetTorch(camera, false)
    }

    private fun doSetTorch(
        camera: Camera,
        newSetting: Boolean
    ) {
        val parameters = camera.parameters
        val flashMode: String?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            /* 是否支持闪光灯 */
            flashMode = if (newSetting) {
                findSettableValue(
                    parameters.supportedFlashModes,
                    Camera.Parameters.FLASH_MODE_TORCH,
                    Camera.Parameters.FLASH_MODE_ON
                )
            } else {
                findSettableValue(
                    parameters.supportedFlashModes,
                    Camera.Parameters.FLASH_MODE_OFF
                )
            }
            if (flashMode != null) {
                parameters.flashMode = flashMode
            }
            camera.parameters = parameters
        }
    }

    @get:RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    val displayOrientation: Int
        get() {
            val info = CameraInfo()
            Camera.getCameraInfo(
                CameraInfo.CAMERA_FACING_BACK,
                info
            )
            val wm =
                mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val rotation = display.rotation
            var degrees = 0
            when (rotation) {
                Surface.ROTATION_0 -> degrees = 0
                Surface.ROTATION_90 -> degrees = 90
                Surface.ROTATION_180 -> degrees = 180
                Surface.ROTATION_270 -> degrees = 270
            }
            var result: Int
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360
                result = (360 - result) % 360
            } else {
                result = (info.orientation - degrees + 360) % 360
            }
            return result
        }

    private fun setZoom(parameters: Camera.Parameters) {
        val zoomSupportedString = parameters["zoom-supported"]
        if (zoomSupportedString != null && !java.lang.Boolean.parseBoolean(zoomSupportedString)) {
            return
        }
        var tenDesiredZoom = TEN_DESIRED_ZOOM
        val maxZoomString = parameters["max-zoom"]
        if (maxZoomString != null) {
            try {
                val tenMaxZoom = (10.0 * maxZoomString.toDouble()).toInt()
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom
                }
            } catch (nfe: NumberFormatException) {
            }
        }
        val takingPictureZoomMaxString = parameters["taking-picture-zoom-max"]
        if (takingPictureZoomMaxString != null) {
            try {
                val tenMaxZoom = takingPictureZoomMaxString.toInt()
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom
                }
            } catch (nfe: NumberFormatException) {
            }
        }
        val motZoomValuesString = parameters["mot-zoom-values"]
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(
                motZoomValuesString,
                tenDesiredZoom
            )
        }
        val motZoomStepString = parameters["mot-zoom-step"]
        if (motZoomStepString != null) {
            try {
                val motZoomStep = motZoomStepString.trim { it <= ' ' }.toDouble()
                val tenZoomStep = (10.0 * motZoomStep).toInt()
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep
                }
            } catch (nfe: NumberFormatException) {
                // continue
            }
        }
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters["zoom"] = (tenDesiredZoom / 10.0).toString()
        }
        if (takingPictureZoomMaxString != null) {
            parameters["taking-picture-zoom"] = tenDesiredZoom
        }
    }

    companion object {
        private const val TEN_DESIRED_ZOOM = 27
        private val COMMA_PATTERN = Pattern.compile(",")
        @RequiresApi(Build.VERSION_CODES.ECLAIR)
        fun autoFocusAble(camera: Camera): Boolean {
            var supportedFocusModes: List<String>? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                supportedFocusModes = camera.parameters.supportedFocusModes
            }
            val focusMode = findSettableValue(
                supportedFocusModes,
                Camera.Parameters.FOCUS_MODE_AUTO
            )
            return focusMode != null
        }

        private fun findSettableValue(
            supportedValues: Collection<String>?,
            vararg desiredValues: String
        ): String? {
            var result: String? = null
            if (supportedValues != null) {
                for (desiredValue in desiredValues) {
                    if (supportedValues.contains(desiredValue)) {
                        result = desiredValue
                        break
                    }
                }
            }
            return result
        }

        private fun getPreviewResolution(
            parameters: Camera.Parameters,
            screenResolution: Point
        ): Point {
            var previewResolution: Point? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                previewResolution = findBestPreviewSizeValue(
                    parameters.supportedPreviewSizes,
                    screenResolution
                )
            }
            if (previewResolution == null) {
                previewResolution = Point(
                    screenResolution.x shr 3 shl 3,
                    screenResolution.y shr 3 shl 3
                )
            }
            return previewResolution
        }

        private fun findBestPreviewSizeValue(
            supportSizeList: List<Camera.Size>,
            screenResolution: Point
        ): Point? {
            var bestX = 0
            var bestY = 0
            var diff = Int.MAX_VALUE
            for (previewSize in supportSizeList) {
                val newX = previewSize.width
                val newY = previewSize.height
                val newDiff =
                    abs(newX - screenResolution.x) + abs(newY - screenResolution.y)
                if (newDiff == 0) {
                    bestX = newX
                    bestY = newY
                    break
                } else if (newDiff < diff) {
                    bestX = newX
                    bestY = newY
                    diff = newDiff
                }
            }
            return if (bestX > 0 && bestY > 0) {
                Point(bestX, bestY)
            } else null
        }

        private fun findBestMotZoomValue(
            stringValues: CharSequence,
            tenDesiredZoom: Int
        ): Int {
            var tenBestValue = 0
            for (stringValue in COMMA_PATTERN.split(
                stringValues
            )) {
                val strValue = stringValue.trim { it <= ' ' }
                val value: Double = try {
                    strValue.toDouble()
                } catch (nfe: NumberFormatException) {
                    return tenDesiredZoom
                }
                val tenValue = (10.0 * value).toInt()
                if (abs(tenDesiredZoom - value) < abs(
                        tenDesiredZoom
                                - tenBestValue
                    )
                ) {
                    tenBestValue = tenValue
                }
            }
            return tenBestValue
        }
    }

}