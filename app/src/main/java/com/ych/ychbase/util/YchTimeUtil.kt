package com.ych.ychbase.util

import android.annotation.SuppressLint
import java.io.UnsupportedEncodingException
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 进行一些转换工作
 *
 * @author lmy
 */
@SuppressLint("SimpleDateFormat")
object YchTimeUtil {

    /**
     * 获取当前时间
     *
     * @return 时间
     */
    val time: String
        get() {
            val dfs = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return dfs.format(Date())
        }

    /**
     * 格式化时间
     *
     * @param time 时间值 (00:00 -23:59:59)
     * @return 时间
     */
    fun formatTime(time: Long): String {
        var temp = time
        if (temp == 0L) {
            return "00:00"
        }
        temp /= 1000
        val s = (temp % 60) // s秒
        val m = (temp / 60 % 60) //m分
        val h = (temp / 60 / 60 % 24) //h小时s
        return if (h > 0) {
            "${if (h > 9) "$h" else "0$h"}:${if (m > 9) "$m" else "0$m"}:${if (s > 9) "$s" else "0$s"}"
        } else {
            "${if (m > 9) "$m" else "0$m"}:${if (s > 9) "$s" else "0$s"}"
        }
    }

    fun formatNetAudioTime(time: Long): String {
        val temp = time
        if (temp == 0L) {
            return "00:00"
        }
        val s = (temp % 60)
        val m = (temp / 60)
        val h = (temp / 60 / 60)
        return if (h > 0) {
            "${if (h > 9) "$h" else "0$h"}:${if (m > 9) "$m" else "0$m"}:${if (s > 9) "$s" else "0$s"}"
        } else {
            "${if (m > 9) "$m" else "0$m"}:${if (s > 9) "$s" else "0$s"}"
        }
    }

    /**
     * 格式化播放次数
     *
     */
    fun formatPlayCount(count: Int): String {
        return when {
            count < 10000 -> "$count"
            else -> {
                "${count / 10000}.${count / 1000 % 10}万"
            }
        }
    }


    /**
     * 格式化时间
     *
     * @param time 时间值 (00:00 -23:59:59)
     * @return 时间
     */
    fun formatDate(time: Long): String {
        val duration = System.currentTimeMillis() - time
        return when {
            duration < 60 * 1000 -> "${duration / 1000}秒前"
            duration < 60 * 1000 * 60 -> "${duration / 1000 / 60}分钟前"
            duration < 60 * 1000 * 24 -> "${duration / 1000 / 60 / 24}小时前"
            else -> {
                val dfs = SimpleDateFormat("yyyy年MM月dd日")
                val date = dfs.format(Date(time))
                date
            }
        }
    }


    /**
     * 格式化文件大小
     *
     * @param size 文件大小值
     * @return 文件大小
     */
    fun formatSize(size: Long): String {
        val df = DecimalFormat("#.00")
        val fileSize: String
        fileSize = when {
            size < 1024 -> df.format(size.toDouble()) + "B"
            size < 1048576 -> df.format(size.toDouble() / 1024) + "KB"
            size < 1073741824 -> df.format(size.toDouble() / 1048576) + "MB"
            else -> df.format(size.toDouble() / 1073741824) + "GB"
        }
        return fileSize
    }

    /**
     * 对乱码的处理
     *
     * @param s 原字符串
     * @return GBK处理后的数据
     */
    fun formatGBKStr(s: String): String {
        var str: String? = null
        try {
            str = String(s.toByteArray(Charsets.ISO_8859_1))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return s
    }

    fun Distance(long1: Double, l1: Double, long2: Double,
                 l2: Double): Float {
        var lat1 = l1
        var lat2 = l2
        val a: Double
        val b: Double = (long1 - long2) * Math.PI / 180.0
        val R = 6378137.0
        // 地球半径
        lat1 = lat1 * Math.PI / 180.0
        lat2 = lat2 * Math.PI / 180.0
        a = lat1 - lat2
        val d: Double
        val sa2: Double
        val sb2: Double
        sa2 = Math.sin(a / 2.0)
        sb2 = Math.sin(b / 2.0)
        d = (2.0
                * R
                * Math.asin(Math.sqrt(sa2 * sa2 + (Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2))))

        return Math.ceil(d).toFloat()
    }

    fun getTimeDifference(starTime: String): String {
        var timeString = ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val dateFormat1 = SimpleDateFormat("yyyy-MM-dd")
        val endTime = dateFormat.format(Date())
        try {
            val parse = dateFormat.parse(starTime)
            val parse1 = dateFormat.parse(endTime)

            val diff = parse1.time - parse.time

            val day = diff / (24 * 60 * 60 * 1000)
            val hour = diff / (60 * 60 * 1000) - day * 24
            val min = diff / (60 * 1000) - day * 24 * 60 - hour * 60
            val s = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60
            val ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                    - min * 60 * 1000 - s * 1000)

            // 距当前时间大于15天时输出年月日
            timeString = when {
                day > 15 -> dateFormat1.format(parse)
                day > 0 -> day.toString() + "天前"
                hour > 0 -> hour.toString() + "小时前"
                min > 0 -> min.toString() + "分钟前"
                else -> s.toString() + "秒前"
            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeString
    }

    /**
     * 毫秒转化成时间
     *
     * @return 时间
     */
    fun distime(time: Long): String {
        val dfs = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(time)
        return dfs.format(date)
    }

    fun custumFormatTime(formatTime: String): String {
        val simple = SimpleDateFormat(formatTime)
        val date = Date()
        return simple.format(date)
    }
}
