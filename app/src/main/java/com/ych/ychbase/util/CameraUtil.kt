package com.ych.ychbase.util

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.UriUtils
import com.soundcloud.android.crop.Crop
import com.ych.ychbase.app.eLog
import com.ych.ychbase.constant.YchConst
import com.ych.ychbase.manager.CacheManager
import java.io.File


/**
 * Created by LiMingYang on 2020/6/8. 22:37
 * Email:lmy1547124279@163.com
 *
 * 相机工具类
 *
 * @author lmy
 */
object CameraUtil {

    private const val TAG = "CameraUtil"

    /** 父级路径 **/
    private var rootPath = CacheManager.picturePath
    private var authority = CacheManager.authority

    /** 拍下来/选择的照片暂时放到临时文件路径 **/
    private var tempPath = rootPath + File.separator + "temp"
    /** 临时文件名 **/
    private var tempName = "YCH-${System.currentTimeMillis()}.jpg"

    private var activity: Activity? = null
    private var fragment: Fragment? = null

    /** 记录是否为头像 **/
    private var type = 0
    /** 是否需要图片剪裁 **/
    private var isNeedCrop: Boolean = true

    /** 记录保存的路径 **/
    private var filePath = when(type) {
        0 -> rootPath + File.separator + "head"
        else -> rootPath + File.separator + "photo"
    }

    /** 记录文件名 **/
    private var fileName = when(type) {
        0 -> "${CacheManager.phone}.jpg"
        else -> "${System.currentTimeMillis()}.jpg"
    }

    /**
     * 初始化
     *
     * @param activity
     * @param type 0：头像，1：其他
     */
    fun init(activity: Activity, type: Int, isNeedCrop: Boolean) {
        this.type = type
        this.isNeedCrop = isNeedCrop
        this.activity = activity
        FileUtils.createOrExistsDir(File(if (isNeedCrop) {
            tempPath
        } else {
            filePath
        }))
    }

    /**
     * 初始化
     *
     * @param fragment
     * @param type 0：头像，1：其他
     */
    fun init(fragment: Fragment, type: Int, isNeedCrop: Boolean) {
        this.type = type
        this.isNeedCrop = isNeedCrop
        this.fragment = fragment
        FileUtils.createOrExistsDir(File(if (isNeedCrop) {
            tempPath
        } else {
            filePath
        }))
    }

    /** 打开相机 **/
    fun openCamera() {
        YchPermission.requestCameraPermission { cameraGranted ->
            if (cameraGranted) {
                YchPermission.requestReadWritePermission { storageGranted ->
                    if (storageGranted) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        val file = if (isNeedCrop) {
                            File(tempPath, tempName)
                        } else {
                            File(filePath, fileName)
                        }
                        val uri = if (Build.VERSION.SDK_INT >= 24) {
                            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            activity?.let {
                                FileProvider.getUriForFile(
                                    it,
                                    authority,
                                    file
                                )
                            }
                            fragment?.let {
                                FileProvider.getUriForFile(
                                    it.context!!,
                                    authority,
                                    file
                                )
                            }
                        } else {
                            Uri.fromFile(file)
                        }
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                        intent.resolveActivity((if (activity != null) {
                            activity!!
                        } else {
                            fragment!!.activity!!
                        }).packageManager)?.let {
                            activity?.let {
                                ActivityUtils.startActivityForResult(
                                    it,
                                    intent,
                                    YchConst.Camera.CAMERA_REQUEST_CODE
                                )
                            }
                            fragment?.let {
                                ActivityUtils.startActivityForResult(
                                    it,
                                    intent,
                                    YchConst.Camera.CAMERA_REQUEST_CODE
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /** 打开相册 **/
    fun openGallery() {
        YchPermission.requestReadWritePermission { isGranted ->
            if (isGranted) {
                val intent = Intent(Intent.ACTION_PICK) // 跳转到 ACTION_IMAGE_CAPTURE
                intent.type = "image/*"
                activity?.startActivityForResult(
                    intent,
                    YchConst.Camera.GALLERY_REQUEST_CODE
                )
                fragment?.startActivityForResult(
                    intent,
                    YchConst.Camera.GALLERY_REQUEST_CODE
                )
            }
        }
    }

    /**
     * 处理剪裁
     *
     * @param requestCode
     * @param resultCode
     * @param result
     * @param cropCallback
     */
    fun handleResult(
        requestCode: Int,
        resultCode: Int,
        result: Intent?,
        cameraGalleryCallback: (file: File) -> Unit = {},
        cropCallback: (file: File) -> Unit = {}
    ) {

        when(resultCode) {
            Activity.RESULT_OK -> {
                when(requestCode) {
                    YchConst.Camera.CAMERA_REQUEST_CODE,
                    YchConst.Camera.GALLERY_REQUEST_CODE -> {
                        if (isNeedCrop) {
                            crop(result?.data)
                        } else {
                            cameraGalleryCallback.invoke(
                                File(filePath, fileName)
                            )
                        }
                    }
                    YchConst.Camera.CROP_REQUEST_CODE -> {
                        val uri = Crop.getOutput(result)
                        val file = UriUtils.uri2File(uri)
                        FileUtils.createOrExistsFile(file)
                        cropCallback.invoke(file)
                    }
                }
            } else -> {
                when(resultCode) {
                    YchConst.Camera.CROP_ERROR_CODE -> {
                        Crop.getError(result).message?.let {
                            eLog("TAG", "发生异常：$it")
                        }
                    }
                }
            }
        }

    }

    /**
     * 开始剪裁
     *
     * @param uri
     */
    private fun crop(uri: Uri?) {
        val file = File(filePath, fileName)
        val destination = Uri.fromFile(file)
        val source = if (uri == null) {
            val sourceFile = File(tempPath, tempName)
            Uri.fromFile(sourceFile)
        } else {
            uri
        }
        activity?.let {
            Crop.of(source, destination)
                .asSquare()
                .start(
                    it,
                    YchConst.Camera.CROP_REQUEST_CODE
                )
        }
        fragment?.let {
            Crop.of(source, destination)
                .asSquare()
                .start(
                    it.context,
                    it,
                    YchConst.Camera.CROP_REQUEST_CODE
                )
        }
    }
}