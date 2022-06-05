package com.ych.ychbase.widget

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.ych.ychbase.R
import com.ych.ychbase.util.RxBus

/**
 * 输入金额的editText
 *
 * @author lmy
 */
class YchAmountEditText : LinearLayoutCompat {

    private var editText: TextInputEditText? = null
    private var textView: AppCompatTextView? = null

    /** 获取当前文字 **/
    var text: String
        set(value) {
            textView?.text = value
        }
        get() = textView?.text.toString()

    /** 记录editText是否显示 **/
    var isEditTextVisible: Boolean = false
    /** 记录textView是否显示 **/
    var isTextViewVisible: Boolean = false

    private var setOnEtChangeListener: () -> Unit = {}
    fun setOnEtChangeListener(setOnEtChangeListener: () -> Unit) {
        this.setOnEtChangeListener = setOnEtChangeListener
    }

    constructor(context: Context): super(context, null)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        val styleAttrs = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.YchAmountEditText
        )

        initView()
        editText?.hint = styleAttrs.getString(R.styleable.YchAmountEditText_et_hint)
        val flag = styleAttrs.getInt(R.styleable.YchAmountEditText_normal_state, 0)
        if (flag == 0) {
            isEditTextVisible = true
            isTextViewVisible = false
            editText?.visibility = View.VISIBLE
            textView?.visibility = View.GONE
        } else {
            isEditTextVisible = false
            isTextViewVisible = true
            editText?.visibility = View.GONE
            textView?.visibility = View.VISIBLE
        }

        styleAttrs.recycle()
    }

    private fun initView() {
        LayoutInflater.from(context)
            .inflate(
                R.layout.layout_ych_editext,
                this
            )

        editText = findViewById(R.id.edit_text)
        val filters = arrayOf<InputFilter>(CashierInputFilter())
        editText?.filters = filters
        editText?.addTextChangedListener({ _, _, _, _ -> }, { _, _, _, _ -> }, {
            textView?.text = it.toString()
            if (isEditTextVisible) {
                setOnEtChangeListener.invoke()
            }
        })

        textView = findViewById(R.id.text_view)
        RxBus.clicks(textView!!) {
            isEditTextVisible = true
            isTextViewVisible = false
            editText?.hint = textView!!.text
            editText?.visibility = View.VISIBLE
            editText?.requestFocus()
            textView?.visibility = View.GONE
        }
    }
}