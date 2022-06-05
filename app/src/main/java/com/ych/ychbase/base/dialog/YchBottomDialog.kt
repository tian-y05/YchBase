package com.ych.ychbase.base.dialog

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * 底部弹框base类
 *
 * @author lmy
 */
abstract class YchBottomDialog constructor(
    context: Context
) : BottomSheetDialog(context) {

    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        initialize()
    }

    /** 初始化 **/
    protected abstract fun initialize()
}