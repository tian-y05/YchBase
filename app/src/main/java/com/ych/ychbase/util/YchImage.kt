package com.ych.ychbase.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.api.load
import coil.transform.CircleCropTransformation
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.StringUtils
import com.ych.ychbase.manager.CacheManager
import java.io.File

/**
 * 加载头像
 *
 * @param placeholder 占位图资源id
 */
fun ImageView.loadHead(@DrawableRes placeholder: Int) {
    val parentPath = CacheManager.picturePath + "/head"
    val photoName = "${CacheManager.phone}.jpg"
    val file = File(parentPath, photoName)
    if (file.exists()) {
        load(file) {
            crossfade(true)
            placeholder(placeholder)
            transformations(CircleCropTransformation())
        }
    }
    val url = CacheManager.headUrl
    if (!StringUtils.isEmpty(url)) {
        if (RegexUtils.isURL(url)) {
            load(url) {
                crossfade(true)
                placeholder(placeholder)
                transformations(CircleCropTransformation())
            }
            return
        }
    }
    setImageResource(placeholder)
}

/**
 * 读取本地图片
 *
 * @param file 要加载的本地图片
 * @param isCircle 是否为圆形
 */
fun ImageView.loadFile(file: File, isCircle: Boolean) {
    loadFile(file, android.R.color.transparent, isCircle)
}

/**
 * 读取本地图片
 *
 * @param file 要加载的本地图片
 * @param placeholder 占位图资源id
 * @param isCircle 是否为圆形
 */
fun ImageView.loadFile(file: File, @DrawableRes placeholder: Int, isCircle: Boolean) {
    if (file.exists()) {
        load(file) {
            crossfade(true)
            placeholder(placeholder)
            if (isCircle) {
                transformations(CircleCropTransformation())
            }
            scaleType = ImageView.ScaleType.FIT_XY
        }
    }
}

/**
 * 读取资源文件图片
 *
 * @param resId 图片资源
 * @param isCircle 是否为圆形
 */
fun ImageView.loadRes(@DrawableRes resId: Int, isCircle: Boolean) {
    load(resId) {
        crossfade(true)
        placeholder(android.R.color.transparent)
        if (isCircle) {
            transformations(CircleCropTransformation())
        }
        scaleType = ImageView.ScaleType.FIT_XY
    }
}

/**
 * 读取drawable资源
 *
 * @param drawable drawable资源
 * @param isCircle 是否为圆形
 */
fun ImageView.loadDrawable(drawable: Drawable, isCircle: Boolean) {
    load(drawable) {
        crossfade(true)
        placeholder(android.R.color.transparent)
        if (isCircle) {
            transformations(CircleCropTransformation())
        }
        scaleType = ImageView.ScaleType.FIT_XY
    }
}