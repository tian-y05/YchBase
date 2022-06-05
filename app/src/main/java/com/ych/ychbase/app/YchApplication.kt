package com.ych.ychbase.app

import android.app.Application
import android.os.Build
import android.os.StrictMode
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import com.tencent.bugly.crashreport.CrashReport
import com.ych.ychbase.manager.AppManager
import com.ych.ychbase.manager.CacheManager
import java.io.File

/**
 * 应用application类
 *
 * @author lmy
 */
open class YchApplication : Application() {

    companion object {
        lateinit var application: Application
        lateinit var environment: YchEnvironment
        lateinit var app: YchApp
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        initUtilCode()
        initStrictMode()
    }

    /**
     * 初始化（app必须调用）
     *
     * @param isDebug 是否为debug模式
     * @param ychApp SHIPPING：船，TRANSPORT：司机
     * @param rootPath app根路径
     * @param env app所处环境
     */
    open fun initialize(isDebug: Boolean, version: String, ychApp: YchApp, rootPath: String, env: YchEnvironment) {
        AppManager.isDebug = isDebug
        AppManager.version = version
        app = ychApp
        environment = env
        CacheManager.authority = if (app == YchApp.SHIPPING) {
            "com.ych.jhshipping.fileprovider"
        }  else {
            "com.ych.jhtransport.fileprovider"
        }

        initBugly()

        val appRootPath = rootPath + File.separator + "files" + File.separator
        CacheManager.appRootPath = appRootPath
        FileUtils.createOrExistsDir(appRootPath)
        CacheManager.picturePath = appRootPath + "pictures"
        FileUtils.createOrExistsDir(CacheManager.picturePath)
        CacheManager.downloadPath = appRootPath + "download"
        FileUtils.createOrExistsDir(CacheManager.downloadPath)
    }

    /** 初始化bugly **/
    private fun initBugly() {
        val appId = if (app == YchApp.SHIPPING) {
            "8a46e77bad"
        } else {
            "5ce7eaf6ec"
        }
        CrashReport.initCrashReport(application, appId, AppManager.isDebug)
    }

    /** 初始化utilCode **/
    private fun initUtilCode() {
        Utils.init(application)
    }

    /** android 7.0系统解决拍照的问题 **/
    private fun initStrictMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            builder.detectFileUriExposure()
        }
    }
}