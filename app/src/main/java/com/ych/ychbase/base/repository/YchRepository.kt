package com.ych.ychbase.base.repository

import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope

/**
 * Repository基类
 *
 * @author lmy
 */
open class YchRepository constructor(vs: CoroutineScope) {

    protected val vsScope = vs

    companion object {
        const val BASE_TAG: String = "YchRepository"
        private const val PAGE_SIZE: Int = 20
        private const val PRE_LOAD_SIZE: Int = 20
    }

    /** 获取paging初始化数据 **/
    open fun getPagingConfig(): PagedList.Config {
        return PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPrefetchDistance(PRE_LOAD_SIZE)
            .setEnablePlaceholders(false)
            .build()
    }
}