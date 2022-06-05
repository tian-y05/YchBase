package com.ych.ychbase.base.activity

import android.content.res.Resources
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.banzhi.statusmanager.StatusManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.ScreenUtils
import com.ych.ychbase.R
import com.ych.ychbase.app.color
import com.ych.ychbase.constant.YchConst
import com.ych.ychbase.dialog.Loading
import com.ych.ychbase.manager.CacheManager
import com.ych.ychbase.util.Watermark
import com.ych.ychbase.widget.YchToolbar

/**
 * activity基类
 *
 * @author lmy
 */
abstract class YchActivity : AppCompatActivity() {

    protected var isNeedScrollToTop = true

    protected var binding: ViewDataBinding? = null

    override fun getResources(): Resources {
        return AdaptScreenUtils.adaptHeight(super.getResources(), YchConst.Common.DESIGN_HEIGHT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenUtils.setPortrait(this)
        if (useDataBinding) {
            binding = DataBindingUtil.setContentView(this, layoutId)
            binding?.lifecycleOwner = this
            binding(binding!!)
        } else {
            setContentView(layoutId)
        }
        initialize()
        if (CacheManager.phone.isNotEmpty()){
            Watermark.getInstance().show(this, "${CacheManager.name} ${CacheManager.phone}");
        }
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
    protected abstract fun initialize()

    /** 初始化intent数据 **/
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
            ActivityUtils.finishActivity(this@YchActivity)
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
        runOnUiThread {
            if (isShow) {
                Loading.show(this)
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