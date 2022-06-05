package com.ych.ychbase.photo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Build
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import net.lucode.hackware.magicindicator.NavigatorHelper
import net.lucode.hackware.magicindicator.NavigatorHelper.OnNavigatorScrollListener
import net.lucode.hackware.magicindicator.abs.IPagerNavigator
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder
import net.lucode.hackware.magicindicator.buildins.UIUtil
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 查看图片的indicator
 *
 * @author lmy
 */
class ScaleCircleNavigator(context: Context) :
    View(context), IPagerNavigator, OnNavigatorScrollListener {
    private var minRadius = 0
    private var maxRadius = 0
    private var normalCircleColor = Color.LTGRAY
    private var selectedCircleColor = Color.GRAY
    private var circleSpacing = 0
    private var circleCount = 0
    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePoints: MutableList<PointF> =
        ArrayList()
    private val circleRadiusArray = SparseArray<Float>()

    // 事件回调
    private var touchable = false
    private var circleClickListener: (index: Int) -> Unit = {}
    private var downX = 0f
    private var downY = 0f
    private var touchSlop = 0
    private var followTouch = true // 是否跟随手指滑动
    private val navigatorHelper = NavigatorHelper()
    private var startInterpolator: Interpolator? = LinearInterpolator()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            init(context)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private fun init(context: Context) {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        minRadius = UIUtil.dip2px(context, 3.0)
        maxRadius = UIUtil.dip2px(context, 5.0)
        circleSpacing = UIUtil.dip2px(context, 8.0)
        navigatorHelper.setNavigatorScrollListener(this)
        navigatorHelper.setSkimOver(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        var result = 0
        when (mode) {
            MeasureSpec.EXACTLY -> result = width
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> result = if (circleCount <= 0) {
                paddingLeft + paddingRight
            } else {
                (circleCount - 1) * minRadius * 2 + maxRadius * 2 + (circleCount - 1) * circleSpacing + paddingLeft + paddingRight
            }
            else -> {
            }
        }
        return result
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        var result = 0
        when (mode) {
            MeasureSpec.EXACTLY -> result = height
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> result =
                maxRadius * 2 + paddingTop + paddingBottom
            else -> {
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        var i = 0
        val j = circlePoints.size
        while (i < j) {
            val point = circlePoints[i]
            val radius = circleRadiusArray[i, minRadius.toFloat()]
            paint.color = ArgbEvaluatorHolder.eval(
                (radius - minRadius) / (maxRadius - minRadius),
                normalCircleColor,
                selectedCircleColor
            )
            canvas.drawCircle(point.x, height / 2.0f, radius, paint)
            i++
        }
    }

    private fun prepareCirclePoints() {
        circlePoints.clear()
        if (circleCount > 0) {
            val y = (height / 2.0f).roundToInt()
            val centerSpacing = minRadius * 2 + circleSpacing
            var startX = maxRadius + paddingLeft
            for (i in 0 until circleCount) {
                val pointF = PointF(startX.toFloat(), y.toFloat())
                circlePoints.add(pointF)
                startX += centerSpacing
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (touchable) {
                downX = x
                downY = y
                return true
            }
            MotionEvent.ACTION_UP -> if (circleClickListener != null) {
                if (abs(x - downX) <= touchSlop && abs(y - downY) <= touchSlop) {
                    var max = Float.MAX_VALUE
                    var index = 0
                    var i = 0
                    while (i < circlePoints.size) {
                        val pointF = circlePoints[i]
                        val offset = abs(pointF.x - x)
                        if (offset < max) {
                            max = offset
                            index = i
                        }
                        i++
                    }
                    circleClickListener.invoke(index)
                }
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        navigatorHelper.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        navigatorHelper.onPageSelected(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        navigatorHelper.onPageScrollStateChanged(state)
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        prepareCirclePoints()
    }

    override fun notifyDataSetChanged() {
        prepareCirclePoints()
        requestLayout()
    }

    override fun onAttachToMagicIndicator() {}
    override fun onDetachFromMagicIndicator() {}
    fun setMinRadius(minRadius: Int) {
        this.minRadius = minRadius
        prepareCirclePoints()
        invalidate()
    }

    fun setMaxRadius(maxRadius: Int) {
        this.maxRadius = maxRadius
        prepareCirclePoints()
        invalidate()
    }

    fun setNormalCircleColor(normalCircleColor: Int) {
        this.normalCircleColor = normalCircleColor
        invalidate()
    }

    fun setSelectedCircleColor(selectedCircleColor: Int) {
        this.selectedCircleColor = selectedCircleColor
        invalidate()
    }

    fun setCircleSpacing(circleSpacing: Int) {
        this.circleSpacing = circleSpacing
        prepareCirclePoints()
        invalidate()
    }

    fun setStartInterpolator(startInterpolator: Interpolator?) {
        this.startInterpolator = startInterpolator
        if (this.startInterpolator == null) {
            this.startInterpolator = LinearInterpolator()
        }
    }

    fun setCircleCount(count: Int) {
        this.circleCount = count // 此处不调用invalidate，让外部调用notifyDataSetChanged
        this.navigatorHelper.totalCount = this.circleCount
    }

    fun setTouchable(touchable: Boolean) {
        this.touchable = touchable
    }

    fun setFollowTouch(followTouch: Boolean) {
        this.followTouch = followTouch
    }

    fun setSkimOver(skimOver: Boolean) {
        this.navigatorHelper.setSkimOver(skimOver)
    }

    fun setCircleClickListener(circleClickListener: (index: Int) -> Unit) {
        if (!touchable) {
            touchable = true
        }
        this.circleClickListener = circleClickListener
    }

    override fun onEnter(
        index: Int,
        totalCount: Int,
        enterPercent: Float,
        leftToRight: Boolean
    ) {
        if (followTouch) {
            val radius =
                minRadius + (maxRadius - minRadius) * startInterpolator!!.getInterpolation(
                    enterPercent
                )
            circleRadiusArray.put(index, radius)
            invalidate()
        }
    }

    override fun onLeave(
        index: Int,
        totalCount: Int,
        leavePercent: Float,
        leftToRight: Boolean
    ) {
        if (followTouch) {
            val radius =
                maxRadius + (minRadius - maxRadius) * startInterpolator!!.getInterpolation(
                    leavePercent
                )
            circleRadiusArray.put(index, radius)
            invalidate()
        }
    }

    override fun onSelected(index: Int, totalCount: Int) {
        if (!followTouch) {
            circleRadiusArray.put(index, maxRadius.toFloat())
            invalidate()
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (!followTouch) {
            circleRadiusArray.put(index, minRadius.toFloat())
            invalidate()
        }
    }
}