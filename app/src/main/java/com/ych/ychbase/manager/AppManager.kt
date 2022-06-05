package com.ych.ychbase.manager

import android.content.Intent
import android.net.Uri
import cn.jpush.android.api.JPushInterface
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.ych.ychbase.app.YchApp
import com.ych.ychbase.app.YchApplication
import com.ych.ychbase.app.context

/**
 * App功能管理类
 *
 * @author lmy
 */
object AppManager {


    private const val KEY_AGREE_PROTOCOL = "key_agree_protocol"
    /** 记录app是否是首次使用 **/
    var isAgreeProtocol: Boolean
        set(value) {
            SPUtils.getInstance().put(KEY_AGREE_PROTOCOL, value)
        }
        get() = SPUtils.getInstance().getBoolean(KEY_AGREE_PROTOCOL, false)

    private const val KEY_IS_TIPS_NOTIFICATION = "key_is_tips_notification"
    /** 是否提示打开通知权限 **/
    var isTipsNotification: Boolean
        set(value) {
            SPUtils.getInstance().put(KEY_IS_TIPS_NOTIFICATION, value)
        }
        get() = SPUtils.getInstance().getBoolean(KEY_IS_TIPS_NOTIFICATION, false)

    private const val KEY_IS_LOGIN = "key_is_login"
    /** 记录app是否登录 **/
    var isLogin: Boolean
        set(value) {
            SPUtils.getInstance().put(KEY_IS_LOGIN, value)
        }
        get() = SPUtils.getInstance().getBoolean(KEY_IS_LOGIN, false)

    private const val KEY_TOKEN = "key_token"
    /** 记录token **/
    var token: String
        set(value) {
            SPUtils.getInstance().put(KEY_TOKEN, value)
        }
        get() = SPUtils.getInstance().getString(KEY_TOKEN, "")

    private const val KEY_IS_DEBUG = "key_is_debug"
    /** 记录当前程序是否是debug状态 **/
    var isDebug: Boolean
        set(value) {
            SPUtils.getInstance().put(KEY_IS_DEBUG, value)
        }
        get() = SPUtils.getInstance().getBoolean(KEY_IS_DEBUG, true)

    private const val KEY_VERSION = "key_version"
    /** 记录当前app版本号 **/
    var version: String
        set(value) {
            SPUtils.getInstance().put(KEY_VERSION, value)
        }
        get() = SPUtils.getInstance().getString(KEY_VERSION, "1.0.0")

    /** 退出登录 **/
    fun logout() {
        JPushInterface.deleteAlias(context, if (YchApplication.app == YchApp.SHIPPING) 1 else 2)
        token = ""
        isLogin = false
        CacheManager.clearCache()
        ActivityUtils.finishAllActivities()
        ActivityUtils.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(if (YchApplication.app == YchApp.SHIPPING) {
                    "activity://shipping_app_login"
                } else {
                    "activity://transport_app_login"
                })
            )
        )
    }
}