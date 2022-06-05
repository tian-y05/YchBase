package com.ych.ychbase.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.banzhi.statusmanager.interfaces.ViewLoader
import com.ych.ychbase.R
import com.ych.ychbase.app.string

/**
 * 空布局
 *
 * @author lmy
 *
 * @param context
 */
class EmptyView(context: Context) : ViewLoader(context) {

    private var ivIcon: AppCompatImageView? = null
    private var tvDesc: AppCompatTextView? = null

    var icon: Int = R.mipmap.icon_empty
        set(value) {
            ivIcon?.setImageResource(value)
        }

    var desc: String = string(R.string.empty_hint)
        set(value) {
            tvDesc?.text = value
            field = value
        }

    @SuppressLint("InflateParams")
    override fun createView(): View {
//        val view = LayoutInflater.from(context)
//            .inflate(R.layout.layout_empty, null, false)
//        ivIcon = view.findViewById(R.id.iv_icon)
//        tvDesc = view.findViewById(R.id.tv_desc)
//        return view
        val frameLayout = FrameLayout(context)
        frameLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val textView = TextView(context)
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.GRAY)
        textView.textSize = 16f
        textView.text = "暂无数据"
        frameLayout.addView(textView)
        return frameLayout
    }
}