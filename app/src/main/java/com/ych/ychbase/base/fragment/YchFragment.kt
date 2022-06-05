package com.ych.ychbase.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.ActivityUtils
import com.ych.ychbase.R
import com.ych.ychbase.app.color
import com.ych.ychbase.dialog.Loading
import com.ych.ychbase.widget.YchToolbar

/**
 * Fragment基类
 *
 * @author lmy
 */
abstract class YchFragment : Fragment() {

    protected var isNeedScrollToTop = true

    private var rootView: View? = null
    protected var binding: ViewDataBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (useDataBinding) {
            binding = DataBindingUtil.inflate(inflater,
                layoutId, container, false)
            binding?.lifecycleOwner = this
            binding(binding!!)
            binding!!.root
        } else {
            if (rootView == null) {
                rootView = inflater.inflate(layoutId, container,
                    false)
            }
            rootView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize(view, savedInstanceState)
    }

    /** 页码 **/
    protected var pageNum: Int = 1
    /** 每页数据 **/
    protected var pageSize: Int = 10
    /** 总页数 **/
    protected var total: Int = 1

    /**
     * 子类必须实现，用于创建 view
     *
     * @return 布局文件 Id
     */
    @get:LayoutRes
    protected abstract val layoutId: Int

    /** 是否使用dataBinding **/
    protected abstract val useDataBinding: Boolean

    protected abstract fun binding(binding: ViewDataBinding)

    /** 初始化 **/
    protected abstract fun initialize(view: View, savedInstanceState: Bundle?)

    protected open fun initIntent() {}

    /** 初始化控件 **/
    protected open fun initView() {}

    /** 初始化数据 **/
    protected open fun initDataSource() {}

    /**
     * 初始化toolbar
     *
     * @param onBackClickListener
     */
    protected open fun YchToolbar.init(
        onBackClickListener: () -> Unit = {}
    ) {
        setOnBackClickListener {
            onBackClickListener.invoke()
            activity?.let { ActivityUtils.finishActivity(it) }
        }
    }

    /**
     * 刷新数据
     *
     * @param refreshListener
     */
    protected open fun SwipeRefreshLayout.refreshData(refreshListener: () -> Unit) {
        setColorSchemeColors(color(R.color.colorAccent))
        setOnRefreshListener {
            refreshListener.invoke()
        }
    }

    protected open fun loadFirstPage() {
        pageNum = 1
        total = 1
    }

    protected open fun loadNextPage() {}

    /** 是否有更多数据 **/
    protected val hasMoreData: Boolean
        get() = pageNum < total

    /**
     * 加载更多
     *
     * @param listener
     */
    protected open fun loadMoreData(listener: () -> Unit) {
        if (hasMoreData) {
            pageNum += 1
            listener.invoke()
        }
    }

    /**
     * 展示加载框
     *
     * @param isShow true：展示 false：隐藏
     */
    protected open fun isLoading(isShow: Boolean) {
        activity?.runOnUiThread {
            if (isShow) {
                Loading.show(activity!!)
            } else {
                Loading.hide()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Loading.hide()
    }
}