package com.ych.ychbase.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.StringUtils
import com.ych.ychbase.R
import com.ych.ychbase.app.color
import com.ych.ychbase.app.string
import com.ych.ychbase.app.toast
import java.util.regex.Pattern

/**
 * Created by LiMingYang on 2020/6/8. 21:29
 * Email:lmy1547124279@163.com
 *
 * 关于UI的工具类
 */

fun RecyclerView.rvScrollListener(loadMoreListener: () -> Unit = {}) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (isPressed && isSlideToBottom()) {
                loadMoreListener.invoke()
            }
        }
    })
}

/**
 * 判断recyclerView是否滑动到底部
 *
 * @return
 */
fun RecyclerView.isSlideToBottom(): Boolean {
    return (computeVerticalScrollExtent() + computeVerticalScrollOffset()
            >= computeVerticalScrollRange())
}

/** 文字复制功能 **/
fun TextView.clip(hint: String = "") {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        val cm: ClipboardManager = context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager
        val cd = ClipData.newPlainText("Label", text)
        cm.setPrimaryClip(cd)
        toast(hint + string(R.string.copy_success))
    }
}

/** 检查String类型是否为null **/
fun checkNull(text: String?): String {
    val str = text
    return if (StringUtils.isEmpty(str)) {
        "--"
    } else {
        str!!
    }
}

/**
 * 关键字高亮
 *
 * @param keyword 关键字
 */
fun TextView.highLightKeyWord(keyword: String) {
    val word = text.toString()
    val sp = SpannableString(word)
    val p = Pattern.compile(keyword)
    val m = p.matcher(word)
    while (m.find()) {
        val start = m.start()
        val end = m.end()
        sp.setSpan(ForegroundColorSpan(
            color(R.color.colorAccent)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    text = sp
}