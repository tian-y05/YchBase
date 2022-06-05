package com.ych.ychbase.util

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.annotation.RequiresApi

/**
 * 软键盘工具类
 *
 * @author lmy
 */
object YchKeyBoardUtil {
    /**
     * 监听键盘弹出/隐藏
     *
     * @param container 容器
     * @param bottomView 最底部的view
     */
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    fun scroll(activity: Activity, container: View, bottomView: View) {
        container.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            //getWindowVisibleDisplayFrame 获取当前窗口可视区域大小
            //getWindowVisibleDisplayFrame 获取当前窗口可视区域大小
            activity.window.decorView
                .getWindowVisibleDisplayFrame(rect)
            val screenHeight: Int =
                activity.window.decorView.height
            //键盘弹出时，可视区域大小改变，屏幕高度 - 窗口可视区域高度 = 键盘弹出高度
            //键盘弹出时，可视区域大小改变，屏幕高度 - 窗口可视区域高度 = 键盘弹出高度
            val softHeight = screenHeight - rect.bottom
            /**
             * 上移的距离 = 键盘的高度 - 按钮距离屏幕底部的高度(如果手机高度很大，上移的距离会是负数，界面将不会上移)
             * 按钮距离屏幕底部的高度是用屏幕高度 - 按钮底部距离父布局顶部的高度
             * 注意这里 btn.getBottom() 是按钮底部距离父布局顶部的高度，这里也就是距离最外层布局顶部高度
             */
            /**
             * 上移的距离 = 键盘的高度 - 按钮距离屏幕底部的高度(如果手机高度很大，上移的距离会是负数，界面将不会上移)
             * 按钮距离屏幕底部的高度是用屏幕高度 - 按钮底部距离父布局顶部的高度
             * 注意这里 btn.getBottom() 是按钮底部距离父布局顶部的高度，这里也就是距离最外层布局顶部高度
             */
            val scrollDistance: Int = softHeight - (screenHeight - bottomView.bottom)
            if (scrollDistance > 0) {
                //具体移动距离可自行调整
                container.scrollTo(0, scrollDistance + 60)
            } else {
                //键盘隐藏，页面复位
                container.scrollTo(0, 0)
            }
        }
    }

    private var rootViewVisibleHeight: Int = 0
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    fun softKeyboardListener(activity: Activity,
                             show: (height: Int) -> Unit = {},
                             hide: (height: Int) -> Unit = {}) {
        // 获取activity的根视图
        val rootView = activity.window.decorView
        // 监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        rootView.viewTreeObserver
            .addOnGlobalLayoutListener(OnGlobalLayoutListener { // 获取当前根视图在屏幕上显示的大小
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val visibleHeight = rect.height()
                if (rootViewVisibleHeight == 0) {
                    rootViewVisibleHeight = visibleHeight
                    return@OnGlobalLayoutListener
                }

                // 根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                if (rootViewVisibleHeight == visibleHeight) {
                    return@OnGlobalLayoutListener
                }

                // 根视图显示高度变小超过200，可以看作软键盘显示了
                if (rootViewVisibleHeight - visibleHeight > 200) {
                    show.invoke(rootViewVisibleHeight - visibleHeight)
                    rootViewVisibleHeight = visibleHeight
                    return@OnGlobalLayoutListener
                }

                // 根视图显示高度变大超过200，可以看作软键盘隐藏了
                if (visibleHeight - rootViewVisibleHeight > 200) {
                    hide.invoke(visibleHeight - rootViewVisibleHeight)
                    rootViewVisibleHeight = visibleHeight
                    return@OnGlobalLayoutListener
                }
            })
    }
}