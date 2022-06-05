package com.ych.ychbase.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.ych.ychbase.R
import com.ych.ychbase.app.string

/**
 * Created by LiMingYang on 2020/7/25. 23:57
 * Email:lmy1547124279@163.com
 */
@SuppressLint("ViewConstructor")
class YchLoadMoreView(
    context: Context,
    @StringRes
    emptyMessage: Int = R.string.empty_hint,
    @DrawableRes
    emptyIcon: Int = R.mipmap.icon_empty
) : LinearLayoutCompat(context), SwipeRecyclerView.LoadMoreView {

    private val message = emptyMessage
    private val icon = emptyIcon

    private var llLoading: LinearLayoutCompat? = null
    private var progressBar: ProgressBar? = null
    private var tvLoadingText: AppCompatTextView? = null

    private var llEmpty: LinearLayoutCompat? = null
    private var ivIcon: AppCompatImageView? = null
    private var tvEmptyText: AppCompatTextView? = null

    init {
        layoutParams = ViewGroup.LayoutParams(-1, -2)
        gravity = Gravity.CENTER
        visibility = View.GONE

        val displayMetrics = resources.displayMetrics

        val minHeight = (displayMetrics.density * 60 + 0.5).toInt()
        minimumHeight = minHeight

        View.inflate(context, R.layout.layout_load_more, this)

        llLoading = findViewById(R.id.ll_loading)
        progressBar = findViewById(R.id.progress_bar)
        tvLoadingText = findViewById(R.id.tv_loading_text)

        llEmpty = findViewById(R.id.ll_empty)
        ivIcon = findViewById(R.id.iv_icon)
        tvEmptyText = findViewById(R.id.tv_empty_text)
    }

    override fun onLoading() {
        visibility = View.VISIBLE
        setPadding(0, 0, 0, 0)
        llLoading?.visibility = View.VISIBLE
        llEmpty?.visibility = View.GONE
        progressBar?.visibility = View.VISIBLE
        tvLoadingText?.visibility = View.VISIBLE
        tvLoadingText?.text = string(R.string.loading_wait_please)
    }

    override fun onLoadFinish(dataEmpty: Boolean, hasMore: Boolean) {
        if (dataEmpty) {
            visibility = View.VISIBLE
            setPadding(0, 80, 0, 80)
            llLoading?.visibility = View.GONE
            llEmpty?.visibility = View.VISIBLE
            ivIcon?.visibility = View.VISIBLE
            ivIcon?.setImageResource(icon)
            tvEmptyText?.visibility = visibility
            tvEmptyText?.text = string(message)
            return
        }

        if (!hasMore) {
            visibility = View.VISIBLE
            setPadding(0, 0, 0, 0)
            llLoading?.visibility = View.VISIBLE
            llEmpty?.visibility = View.GONE
            progressBar?.visibility = View.GONE
            ivIcon?.visibility = View.GONE
            tvLoadingText?.visibility = visibility
            tvLoadingText?.text = string(R.string.no_more_data)
        } else {
            visibility = View.INVISIBLE
        }
    }

    override fun onWaitToLoadMore(loadMoreListener: SwipeRecyclerView.LoadMoreListener?) {}

    override fun onLoadError(errorCode: Int, errorMessage: String?) {}
}