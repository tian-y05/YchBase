package com.ych.ychbase.zxing.core

import android.hardware.Camera
import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
open class ProcessDataTask(
    private val mCamera: Camera,
    private val mData: ByteArray,
    private var mDelegate: Delegate?,
    private val orientation: Int
) : AsyncTask<Void?, Void?, String?>() {
    fun perform(): ProcessDataTask {
        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor(THREAD_POOL_EXECUTOR)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                execute()
            }
        }
        return this
    }

    fun cancelTask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            if (status != Status.FINISHED) {
                cancel(true)
            }
        }
    }

    override fun onCancelled() {
        super.onCancelled()
        mDelegate = null
    }

    override fun doInBackground(vararg params: Void?): String? {
        val parameters = mCamera.parameters
        val size = parameters.previewSize
        var width = size.width
        var height = size.height
        var data = mData
        if (orientation == BGAQRCodeUtil.ORIENTATION_PORTRAIT) {
            data = ByteArray(mData.size)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    data[x * height + height - y - 1] = mData[x + y * width]
                }
            }
            val tmp = width
            width = height
            height = tmp
        }
        return try {
            if (mDelegate == null) {
                null
            } else mDelegate!!.processData(data, width, height, false)
        } catch (e1: Exception) {
            try {
                mDelegate!!.processData(data, width, height, true)
            } catch (e2: Exception) {
                null
            }
        }
    }

    interface Delegate {
        fun processData(
            data: ByteArray?,
            width: Int,
            height: Int,
            isRetry: Boolean
        ): String?
    }

}