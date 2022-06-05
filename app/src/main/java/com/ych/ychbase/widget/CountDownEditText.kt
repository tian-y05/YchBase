package com.ych.ychbase.widget

import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.ych.ychbase.R

/**
 * 易船货计数功能的editText
 *
 * @author lmy
 */
class CountDownEditText : LinearLayoutCompat {

    /** 标题布局 **/
    private var llLabel: LinearLayoutCompat? = null
    /** 标题 **/
    private var tvLabel: AppCompatTextView? = null

    /** 输入框 **/
    private var etContent: AppCompatEditText? = null

    /** 最小值 **/
    private var tvMin: AppCompatTextView? = null
    /** 最大值 **/
    private var tvMax: AppCompatTextView? = null

    constructor(context: Context): super(context, null)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initView()

        val styleAttrs = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.CountDownEditText
        )
        val isVisible = styleAttrs.getBoolean(R.styleable.CountDownEditText_isVisible, true)
        llLabel?.visibility = if (isVisible) View.VISIBLE else View.GONE

        val label = styleAttrs.getString(R.styleable.CountDownEditText_label)
        tvLabel?.text = label

        val contentHint = styleAttrs.getString(R.styleable.CountDownEditText_hint)

        val max = styleAttrs.getInteger(R.styleable.CountDownEditText_maxValue, 120)
        tvMax?.text = max.toString()

        etContent?.apply {
            filters = arrayOf<InputFilter>(LengthFilter(max))

            hint = contentHint
            addTextChangedListener({ _, _, _, _ ->  },
                { text, start, count, after ->
                    text?.let {
                        val length = it.length
                        if (length <= max) {
                            tvMin?.text = length.toString()
                        }
                    }
                    onTextChanged.invoke(text, start, count, after)
                }, {}
            )
        }

        styleAttrs.recycle()
    }

    private var onTextChanged: (
        text: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) -> Unit = { _, _, _, _ ->  }
    fun onTextChangedListener(onTextChanged: (
        text: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) -> Unit) {
        this.onTextChanged = onTextChanged
    }

    /** 提供给外界输入内容 **/
    val text get() = etContent?.text.toString()

    /** 初始化控件 **/
    private fun initView() {
        LayoutInflater.from(context)
            .inflate(
                R.layout.layout_count_down_edittext,
                this
            )

        llLabel = findViewById(R.id.ll_label)
        tvLabel = findViewById(R.id.tv_label)

        etContent = findViewById(R.id.edit_text_content)

        tvMin = findViewById(R.id.tv_min)
        tvMax = findViewById(R.id.tv_max)
    }
}