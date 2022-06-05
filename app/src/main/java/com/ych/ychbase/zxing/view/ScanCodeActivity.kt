package com.ych.ychbase.zxing.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.ActivityUtils
import com.ych.ychbase.R
import com.ych.ychbase.base.activity.YchActivity
import com.ych.ychbase.util.RxBus
import com.ych.ychbase.zxing.core.QRCodeView
import kotlinx.android.synthetic.main.activity_scan.*

class ScanCodeActivity : YchActivity() {

    companion object {
        const val CODE_SCAN = 2001
        const val KEY_SCAN_RESULT = "key_scan_result"
    }

    override val layoutId: Int
        get() = R.layout.activity_scan

    override val useDataBinding: Boolean
        get() = false

    override fun binding(binding: ViewDataBinding) {}

    override fun initialize() {
        initView()
    }

    override fun onStart() {
        super.onStart()
        scan_view.startSpot()
    }

    override fun initView() {
        super.initView()

        RxBus.clicks(iv_back) {
            ActivityUtils.finishActivity(ScanCodeActivity::class.java)
        }

        scan_view.setDelegate(object : QRCodeView.Delegate {
            override fun onScanQRCodeSuccess(result: String?) {
                vibrate()
                val intent = Intent()
                intent.putExtra(KEY_SCAN_RESULT, result)
                setResult(Activity.RESULT_OK, intent)
                ActivityUtils.finishActivity(ScanCodeActivity::class.java)
            }

            override fun onScanQRCodeOpenCameraError() {

            }
        })
    }

    /** 震动 **/
    private fun vibrate() {
        val vibrator = getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, 0x8F))
        } else {
            vibrator.vibrate(200)
        }
    }

    override fun onStop() {
        scan_view.stopCamera()
        super.onStop()
    }
}