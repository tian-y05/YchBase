package com.ych.ychbase.manager

import android.os.Handler
import android.os.Looper
import com.ych.ychbase.app.eLog
import java.util.concurrent.*

/**
 * 线程池管理类
 *
 * @author lmy
 */
object ExecutorManager {
    private const val TAG: String = "ExecutorManager"

    /**
     * 磁盘IO线程池（单线程）
     *
     * 和磁盘操作有关的进行使用此线程(如读写数据库,读写文件)
     * 禁止延迟,避免等待
     * 此线程不用考虑同步问题
     *
     * @return
     */
    val diskIo by lazy { diskIoExecutor() }

    /**
     * 网络IO线程池
     *
     * 网络请求,异步任务等适用此线程
     * 不建议在这个线程 sleep 或者 wait
     *
     * @return
     */
    val networkIo by lazy { networkExecutor() }

    /**
     * UI线程
     *
     * Android 的MainThread
     * UI线程不能做的事情这个都不能做
     *
     * @return
     */
    val mainThread by lazy { MainThreadExecutor() }

    /**
     * 定时(延时)任务线程池
     *
     * @return
     */
    val scheduledExecutor by lazy { scheduledThreadPoolExecutor() }

    private fun diskIoExecutor(): ExecutorService {
        return ThreadPoolExecutor(
            1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(1024),
            { r -> Thread(r, "disk_executor") },
            { _, _ -> eLog(TAG, "rejectedExecution: disk io executor queue overflow") })
    }

    private fun networkExecutor(): ExecutorService {
        return ThreadPoolExecutor(
            3,
            6,
            1000,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(6),
            { r -> Thread(r, "network_executor") },
            { _, _ -> eLog(TAG, "rejectedExecution: network executor queue overflow") })
    }

    class MainThreadExecutor: Executor {
        private val handler: Handler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable?) {
            command?.let {
                handler.post(it)
            }
        }
    }

    private fun scheduledThreadPoolExecutor(): ScheduledExecutorService {
        return ScheduledThreadPoolExecutor(
            16,
            { r -> Thread(r, "scheduled_executor") },
            { _, _ -> eLog(TAG, "rejectedExecution: scheduled executor queue overflow") })
    }
}