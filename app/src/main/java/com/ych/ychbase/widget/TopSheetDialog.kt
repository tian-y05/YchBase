package com.ych.ychbase.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat

/**
 * Created by LiMingYang on 2020/7/28. 22:28
 * Email:lmy1547124279@163.com
 *
 * 顶部弹出dialog
 */
class TopSheetDialog : LinearLayoutCompat {

    private var isOpen: Boolean = false
    private var dropView: ViewGroup? = null
    private var dropHeight: Int? = null

    constructor(context: Context): super(context, null) {
        orientation = VERTICAL
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs, 0) {
        orientation = VERTICAL
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        orientation = VERTICAL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (null != getChildAt(1) && getChildAt(1) is ViewGroup) {
            val rootDropView = getChildAt(1) as ViewGroup
            val childCount = rootDropView.childCount
            if (childCount > 0) {
                dropView = rootDropView.getChildAt(0) as ViewGroup
                dropHeight = dropView!!.measuredHeight
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    dropView?.y = -dropHeight!!.toFloat()
                }
            }
        }
    }

    /** 是否显示 **/
    val isShowing: Boolean get() = isOpen

    @SuppressLint("ObjectAnimatorBinding")
    fun show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val translationY =
                ObjectAnimator.ofFloat(
                    dropView,
                    "translationY",
                    -dropHeight!!.toFloat(),
                    0f
                )
            translationY.duration = 300
            translationY.start()
            isOpen = true
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun dismiss() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val translationY =
                ObjectAnimator.ofFloat(
                    dropView,
                    "translationY",
                    0f,
                    -dropHeight!!.toFloat()
                )
            translationY.duration = 300
            translationY.start()
            isOpen = false
        }
    }
}