package com.ych.ychbase.app

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.ych.ychbase.manager.ExecutorManager
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/** 全局context **/
val context: Context get() = Utils.getApp()

/**
 * 获取资源文件中的字符串
 *
 * @param resId 资源id
 */
fun string(@StringRes resId: Int): String = context.getString(resId)

/**
 * 获取资源文件中的颜色
 *
 * @param resId 资源id
 */
fun color(@ColorRes resId: Int): Int = ContextCompat.getColor(context, resId)

/**
 * 弹出短toast
 *
 * @param message 提示信息
 */
fun toast(message: String) {
    ToastUtils.showShort(message)
}

/**
 * 弹出短toast
 *
 * @param resId 资源id
 */
fun toast(@StringRes resId: Int) {
    ToastUtils.showShort(resId)
}

/**
 * log.i
 *
 * @param content log内容
 */
fun iLog(vararg content: Any) {
    LogUtils.i(content)
}

/**
 * log.i
 *
 * @param tag log tag
 * @param content log内容
 */
fun iLog(tag: String, vararg content: Any) {
    LogUtils.iTag(tag, content)
}

/**
 * log.d
 *
 * @param content log内容
 */
fun dLog(vararg content: Any) {
    LogUtils.d(content)
}

/**
 * log.d
 *
 * @param tag log tag
 * @param content log内容
 */
fun dLog(tag: String, vararg content: Any) {
    LogUtils.dTag(tag, content)
}

/**
 * log.e
 *
 * @param content log内容
 */
fun eLog(vararg content: Any) {
    LogUtils.e(content)
}

/**
 * log.e
 *
 * @param tag log tag
 * @param content log内容
 */
fun eLog(tag: String, vararg content: Any) {
    LogUtils.eTag(tag, content)
}

/**
 * 运行在Io线程
 *
 * @param function
 */
fun runOnIoThread(function: () -> Unit) {
    ExecutorManager.diskIo.execute(function)
}

/**
 * 运行在网络线程
 *
 * @param function
 */
fun runOnNetworkThread(function: () -> Unit) {
    ExecutorManager.networkIo.execute(function)
}

/**
 * 运行在主线程
 *
 * @param function
 */
fun runOnMainThread(function: () -> Unit) {
    ExecutorManager.mainThread.execute(function)
}

/**
 * 运行在定时/延时线程
 *
 * @param function
 * @param delayTime 延时
 * @param intervalTime 定时
 */
fun runScheduleThread(function: () -> Unit, delayTime: Long, intervalTime: Long): ScheduledFuture<*> {
    return ExecutorManager.scheduledExecutor.scheduleAtFixedRate(
        function,
        delayTime,
        intervalTime,
        TimeUnit.MILLISECONDS
    )
}