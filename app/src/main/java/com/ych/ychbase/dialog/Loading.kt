package com.ych.ychbase.dialog

import android.content.Context
import androidx.appcompat.app.AppCompatDialog
import com.ych.ychbase.R

/**
 * 加载框
 *
 * @author lmy
 */
object Loading {

    private var dialog: AppCompatDialog? = null

    fun show(context: Context) {
        dialog = AppCompatDialog(context, R.style.YchDialog)
        dialog?.apply {
            setContentView(R.layout.layout_loading)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            show()
        }
    }

    fun hide() {
        dialog?.apply {
            if (isShowing) dismiss()
        }
    }
}