package com.ych.ychbase.util

import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.NetworkUtils
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.ych.ychbase.R
import com.ych.ychbase.app.eLog
import com.ych.ychbase.app.string
import com.ych.ychbase.app.toast
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit

/**
 * Rx工具类
 *
 * @author lmy
 */
object RxBus {

    private const val TAG = "RxBus"
    /** 点击时长 **/
    private const val clickDuration: Long = 1

    /**
     * 处理防止连续点击事件
     *
     * @param view
     * @param callback
     */
    fun clicks(view: View, callback: (View) -> Unit = {}) {
        clicks(view, clickDuration, callback)
    }

    /**
     * 处理防止连续点击事件
     *
     * @param view
     * @param duration
     * @param callback
     */
    private fun clicks(view: View, duration: Long, callback: (View) -> Unit = {}) {
        RxView.clicks(view)
            .throttleFirst(duration, TimeUnit.SECONDS)
            .subscribe(object : Observer<Any> {

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Any) {
                    callback.invoke(view)
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }
            })
    }

    /**
     * 处理防止连续点击事件
     *
     * @param view
     * @param callback
     */
    fun clicks(vararg view: View, callback: (View) -> Unit = {}) {
        view.forEach {
            RxView.clicks(it)
                .throttleFirst(clickDuration, TimeUnit.SECONDS)
                .subscribe(object : Observer<Any> {

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Any) {
                        callback.invoke(it)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
        }
    }


    /** 倒计时时间 **/
    private const val MAX_COUNT_TIME: Long = 60
    private var countDownDisposable: Disposable? = null

    /**
     * 倒计时
     *
     * @param view
     * @param listener
     */
    fun countDown(view: TextView, listener: (time: Long) -> Unit = {}) {
        countDownDisposable = RxView.clicks(view)
            .throttleFirst(2, TimeUnit.SECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            // 判断手机号否为空
            .flatMap {
                if (!NetworkUtils.isConnected()) {
                    toast(R.string.network_error)
                    return@flatMap Observable.empty<Boolean>()
                }
                return@flatMap Observable.just(true)
            }
            .flatMap { // 将点击事件转换成倒计时事件
                RxView.enabled(view).accept(false) // 更新发送按钮的状态并初始化显现倒计时文字
                val formatStr = string(R.string.count_down_hint)
                val countDownHint = String.format(formatStr, MAX_COUNT_TIME)
                RxTextView.text(view).accept(countDownHint)
                Observable.interval(1, TimeUnit.SECONDS, Schedulers.io()) // 在实际操作中可以在此发送获取网络的请求,,续1s
                    .take(MAX_COUNT_TIME)
                    .map { t -> // 将递增数字替换成递减的倒计时数字
                        listener.invoke(t + 1)
                        MAX_COUNT_TIME - (t + 1)
                    }
            }
            .observeOn(AndroidSchedulers.mainThread()) // 切换到 Android 的主线程。
            .subscribe { aLong ->
                if (aLong == 0L) {
                    RxView.enabled(view).accept(true)
                    RxTextView.text(view).accept(string(R.string.re_get))
                } else {
                    RxView.enabled(view).accept(false)
                    val formatStr = string(R.string.count_down_hint)
                    val countDownHint = String.format(formatStr, aLong)
                    RxTextView.text(view).accept(countDownHint)
                }
            }
    }

    /** 延时时间 **/
    private const val DELAY_TIME: Long = 0
    /** 间隔时间 **/
    private const val PERIOD_TIME: Long = 1
    /**
     * 倒计时
     *
     * @param view
     */
    fun countDown(view: TextView) {
        var subscription: Subscription? = null // 用于取消订阅关系，防止内存泄露
        Flowable.interval(DELAY_TIME, PERIOD_TIME, TimeUnit.SECONDS) // 设置0延迟，每隔1秒发送一条数据
            .onBackpressureBuffer() // 背压策略
            .take(MAX_COUNT_TIME) // 循环次数
            .map { MAX_COUNT_TIME - it }
            .observeOn(AndroidSchedulers.mainThread()) // 在ui线程执行ui
            .subscribe(object : Subscriber<Long> {
                override fun onSubscribe(s: Subscription?) {
                    view.isEnabled = false
                    subscription = s
                    subscription?.request(Long.MAX_VALUE) // 设置请求事件的数量
                }

                override fun onNext(t: Long?) {
                    val formatStr = string(R.string.count_down_hint)
                    val countDownHint = String.format(formatStr, t)
                    view.text = countDownHint
                }

                override fun onComplete() {
                    view.isEnabled = true
                    view.text = string(R.string.re_get)
                    subscription?.cancel() // 取消订阅，防止内存泄漏
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    eLog(TAG, "${t?.message}")
                }

            })
    }

    /** 取消倒计时 **/
    fun destroyCountDown() {
        countDownDisposable?.dispose()
    }
}