package com.ych.ychbase.util

import android.content.Context
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.ych.ychbase.app.eLog
import com.ych.ychbase.app.toast

/**
 * 高德地图工具类
 *
 * @author lmy
 */
object AMapUtil {
    private const val TAG = "YchAMapUtil"

    private var context: Context? = null

    private var aMapLocationClient: AMapLocationClient? = null
    private var aMapLocationClientOption: AMapLocationClientOption? = null

    private var query: PoiSearch.Query? = null
    private var searchLatLonPoint: LatLonPoint? = null
    private var poiSearch: PoiSearch? = null

    private var total: Int = 0

    private var onLocationListener: (location: AMapLocation) -> Unit = {}
    fun onLocation(onLocationListener: (location: AMapLocation) -> Unit) {
        this.onLocationListener = onLocationListener
    }

    /**
     * 初始化
     *
     * @param context
     */
    fun init(context: Context) {
        this.context = context
        aMapLocationClient = AMapLocationClient(context)
        aMapLocationClient?.setLocationListener {
            it?.let { location ->
                if (location.errorCode == 0) {
                    onLocationListener.invoke(location)
                } else {
                    eLog(
                        TAG,
                        "errCode：${location.errorCode}，errInfo：${location.errorInfo}"
                    )
                }
            }
        }

        aMapLocationClientOption = AMapLocationClientOption()
        // 设置高精度定位
        aMapLocationClientOption?.isOnceLocation = true
        aMapLocationClientOption?.isMockEnable = false
        aMapLocationClientOption?.locationPurpose =
            AMapLocationClientOption.AMapLocationPurpose.SignIn
        aMapLocationClientOption?.locationMode =
            AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        aMapLocationClientOption?.interval = 2000
        // 设置定位参数
        aMapLocationClient?.setLocationOption(aMapLocationClientOption)

        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        aMapLocationClient?.stopLocation()
        aMapLocationClient?.startLocation()
    }

    /**
     * 搜索
     *
     * @param content 搜索内容
     * @param location 位置信息
     */
    fun search(content: String, pageNum: Int, location: AMapLocation) {
        query = PoiSearch.Query(content, "", location.city)
        query?.apply {
            cityLimit = true
            pageSize = 20 // 设置每页最多返回多少条poiItem
            this.pageNum = pageNum // 设置查询页码
        }
        poiSearch = PoiSearch(context, query)

        searchLatLonPoint = LatLonPoint(location.longitude, location.latitude)
        searchLatLonPoint?.let {
            poiSearch?.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
                override fun onPoiItemSearched(poiItem: PoiItem?, i: Int) {}

                override fun onPoiSearched(poiResult: PoiResult?, resultCode: Int) {
                    val list = ArrayList<PoiItem>()
                    if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
                        if (poiResult != null && poiResult.query != null) {
                            if (poiResult.query == query) {
                                total = poiResult.pageCount
                                list.addAll(poiResult.pois)
                            }
                        }
                    } else {
                        eLog(TAG, "$poiResult")
                    }
                    onSearchListener.invoke(list, total)
                }
            })
            poiSearch?.bound = PoiSearch.SearchBound(it, 10000, true)
            poiSearch?.searchPOIAsyn()
        }
    }

    private var onSearchListener: (list: ArrayList<PoiItem>, total: Int) -> Unit = { _, _ -> }
    fun onSearch(onSearchListener: (list: ArrayList<PoiItem>, total: Int) -> Unit) {
        this.onSearchListener = onSearchListener
    }

    /** 销毁定位 **/
    fun destroy() {
        aMapLocationClient?.stopLocation()
        aMapLocationClient?.onDestroy()
        aMapLocationClient = null
        aMapLocationClientOption = null
    }
}