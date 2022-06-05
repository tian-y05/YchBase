package com.ych.ychbase.widget

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.ych.ychbase.R
import com.ych.ychbase.app.color
import com.ych.ychbase.util.RxBus

/**
 * 易船货自定义toolbar
 *
 * @author lmy
 */
class YchToolbar : LinearLayoutCompat {

    private var ivBack: AppCompatImageView? = null
    private var tvTitle: AppCompatTextView? = null

    private var tvEnd: AppCompatTextView? = null

    private var ivEnd: AppCompatImageView? = null

    private var etContent: AppCompatEditText? = null
    private var tvSearch: AppCompatTextView? = null

    constructor(context: Context): super(context, null)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        val styleAttrs = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.YchToolbar
        )
        val type = styleAttrs.getInt(R.styleable.YchToolbar_style, 0)
        initLayoutByType(type)
        initViewByType(type, styleAttrs)

        styleAttrs.recycle()
    }

    /**
     * 根据类型初始化布局
     *
     * @param type 布局类型
     */
    private fun initLayoutByType(type: Int) {
        LayoutInflater.from(context)
            .inflate(when(type) {
                0 -> R.layout.layout_default_toolbar
                1 -> R.layout.layout_without_back_right_icon_toolbar
                2 -> R.layout.layout_end_text_toolbar
                3 -> R.layout.layout_end_icon_toolbar
                4 -> R.layout.layout_search_toolbar
                5 -> R.layout.layout_only_title_toolbar
                6 -> R.layout.layout_left_title_right_text_toolbar
                else -> R.layout.layout_default_toolbar
            }, this)
    }

    /**
     * 根据类型初始化控件
     *
     * @param type
     */
    private fun initViewByType(type: Int, styleAttrs: TypedArray) {
        setBackgroundResource(styleAttrs.getResourceId(R.styleable.YchToolbar_bg_res, R.color.colorAccent))

        tvTitle = findViewById(R.id.tv_title)
        val title = styleAttrs.getString(R.styleable.YchToolbar_title)
        tvTitle?.text = title

        when(type) {
            0, 2, 3, 4 -> {
                ivBack = findViewById(R.id.iv_back)
                RxBus.clicks(ivBack!!) {
                    onBackClickListener.invoke()
                }
            }
        }

        when(type) {
            2, 6 -> {
                tvEnd = findViewById(R.id.tv_end)
                val rightText = styleAttrs.getString(R.styleable.YchToolbar_right_text)
                tvEnd?.apply {
                    text = rightText
                    RxBus.clicks(this) {
                        onEndClickListener.invoke()
                    }
                }
            }
            1, 3 -> {
                ivEnd = findViewById(R.id.iv_end)
                val rightIcon = styleAttrs.getDrawable(R.styleable.YchToolbar_right_icon)
                rightIcon?.let {
                    ivEnd?.apply {
                        setImageDrawable(it)
                        RxBus.clicks(this) {
                            onEndClickListener.invoke()
                        }
                    }
                }
            }
            4 -> {
                etContent = findViewById(R.id.et_search)
                tvSearch = findViewById(R.id.tv_search)
                val searchHint = styleAttrs.getString(R.styleable.YchToolbar_search_hint)
                etContent?.hint = searchHint
                etContent?.requestFocus()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                    etContent?.setOnEditorActionListener { _, actionId, _ ->
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            onSearchClickListener.invoke(searchContent)
                            return@setOnEditorActionListener true
                        }
                        return@setOnEditorActionListener false
                    }
                }
                RxBus.clicks(tvSearch!!) {
                    onSearchClickListener.invoke(searchContent)
                }
            }
        }
    }

    private var onBackClickListener: () -> Unit = {}

    /**
     * 提供给外界返回按钮监听
     *
     * @param onBackClickListener
     */
    fun setOnBackClickListener(onBackClickListener: () -> Unit) {
        this.onBackClickListener = onBackClickListener
    }

    private var onEndClickListener: () -> Unit = {}

    /**
     * 提供给外界右侧按钮监听
     *
     * @param onEndClickListener
     */
    fun setOnEndClickListener(onEndClickListener: () -> Unit) {
        this.onEndClickListener = onEndClickListener
    }

    private var onSearchClickListener: (content: String) -> Unit = {}

    /**
     * 提供给外界搜索按钮点击
     *
     * @param onSearchClickListener
     */
    fun setOnSearchClickListener(onSearchClickListener: (content: String) -> Unit) {
        this.onSearchClickListener = onSearchClickListener
    }

    /** 提供给外界的搜索内容 **/
    val searchContent
        get() = etContent?.text.toString()

    /**
     * 设置标题
     */
    var title
        set(value) {
            tvTitle?.text = value
        }
        get() = tvTitle?.text
}