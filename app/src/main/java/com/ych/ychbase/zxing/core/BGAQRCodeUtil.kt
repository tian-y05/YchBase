package com.ych.ychbase.zxing.core

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.TypedValue
import android.view.WindowManager

object BGAQRCodeUtil {
    const val ORIENTATION_PORTRAIT = 0
    private const val ORIENTATION_LANDSCAPE = 1

    @JvmStatic
    fun getOrientation(context: Context): Int {
        val screenResolution = getScreenResolution(context)
        return if (screenResolution.x > screenResolution.y) ORIENTATION_LANDSCAPE else ORIENTATION_PORTRAIT
    }

    @JvmStatic
    fun getScreenResolution(context: Context): Point {
        val wm =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val screenResolution = Point()
        if (Build.VERSION.SDK_INT >= 13) {
            display.getSize(screenResolution)
        } else {
            screenResolution[display.width] = display.height
        }
        return screenResolution
    }

    @JvmStatic
    fun dp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            context.resources.displayMetrics
        ).toInt()
    }

    @JvmStatic
    fun sp2px(context: Context, spValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spValue,
            context.resources.displayMetrics
        ).toInt()
    }

    @JvmStatic
    fun adjustPhotoRotation(inputBitmap: Bitmap?, orientationDegree: Int): Bitmap? {
        if (inputBitmap == null) {
            return null
        }
        val matrix = Matrix()
        matrix.setRotate(
            orientationDegree.toFloat(),
            inputBitmap.width.toFloat() / 2,
            inputBitmap.height.toFloat() / 2
        )
        val outputX: Float
        val outputY: Float
        if (orientationDegree == 90) {
            outputX = inputBitmap.height.toFloat()
            outputY = 0f
        } else {
            outputX = inputBitmap.height.toFloat()
            outputY = inputBitmap.width.toFloat()
        }
        val values = FloatArray(9)
        matrix.getValues(values)
        val x1 = values[Matrix.MTRANS_X]
        val y1 = values[Matrix.MTRANS_Y]
        matrix.postTranslate(outputX - x1, outputY - y1)
        val outputBitmap = Bitmap.createBitmap(
            inputBitmap.height,
            inputBitmap.width,
            Bitmap.Config.ARGB_8888
        )
        val paint = Paint()
        val canvas = Canvas(outputBitmap)
        canvas.drawBitmap(inputBitmap, matrix, paint)
        return outputBitmap
    }

    @JvmStatic
    fun makeTintBitmap(inputBitmap: Bitmap?, tintColor: Int): Bitmap? {
        if (inputBitmap == null) {
            return null
        }
        val outputBitmap = Bitmap.createBitmap(
            inputBitmap.width,
            inputBitmap.height,
            inputBitmap.config
        )
        val canvas = Canvas(outputBitmap)
        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(inputBitmap, 0f, 0f, paint)
        return outputBitmap
    }
}