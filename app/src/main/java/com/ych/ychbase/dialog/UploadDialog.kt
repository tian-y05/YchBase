package com.ych.ychbase.dialog

import android.content.Context
import com.ych.ychbase.R
import com.ych.ychbase.base.dialog.YchBottomDialog
import com.ych.ychbase.util.RxBus
import kotlinx.android.synthetic.main.dialog_upload.*

/**
 * 头像上传
 *
 * @author lmy
 */
class UploadDialog constructor(context: Context) : YchBottomDialog(context) {

    private var onItemSelectListener: (witch: Int) -> Unit = {}
    fun setOnItemSelectListener(onItemSelectListener: (witch: Int) -> Unit) {
        this.onItemSelectListener = onItemSelectListener
    }

    override val layoutId: Int
        get() = R.layout.dialog_upload

    override fun initialize() {
        RxBus.clicks(tv_camera, tv_gallery) {
            val witch = when(it) {
                tv_camera -> 0
                else -> 1
            }
            onItemSelectListener.invoke(witch)
            dismiss()
        }
        RxBus.clicks(tv_cancel) { dismiss() }
    }
}