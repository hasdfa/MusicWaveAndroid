package com.vadim.hasdfa.musicwave.view.wavebar

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.vadim.hasdfa.musicwave.R
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes

class WaveSideBar: FrameLayout {

    var onStateChangeListener: OnStateChangeListener? = null
    var orientation: Orientation = Orientation.Left
        set(value) {
            if (field != value) {
                if (value == Orientation.Left) {
                    rotation = 0f
                    view?.rotation = 0f
                } else {
                    rotation = 180f
                    view?.rotation = -180f
                }
            }

            field = value
        }

    var expandAnimationDuration = 800L
    var collapseAnimationDuration = 0L

    var view: View? = null
        set(value) {
            field = value
            addView(value)
            value?.visibility = View.GONE
            value?.rotation = if (orientation == Orientation.Left) 0f else -180f
            init()
        }

    @ColorRes
    var startColorRes = android.R.color.white
        get() = ContextCompat.getColor(context, field)

    @ColorRes
    var endColorRes = android.R.color.white
        get() = ContextCompat.getColor(context, field)

    var barWidthPixels: Int
            = context.resources.getDimensionPixelOffset(R.dimen.side_bar_width)

    var isExpanded = false
    var isBusy = false

    private var startX = 0f
    private var startY = 0f

    var minXCoef: Float = 0f
    var minYCoef: Float = 0f

    private var crrX = 0f
    val minX by lazy { width * minXCoef }
    var currentX
        get() = crrX
        set(value) {
            crrX = if (value < minX) minX else value
        }

    private var crrY = 0f
    val minY by lazy { height * minYCoef }
    var currentY
        get() = crrY
        set(value) {
            crrY = if (value == 0f) minY else value
        }

    private var controlX = 0f

    private var zeroX = 0f
    private var invertedFraction = 1f

    private var smallOffset = dpToPx(R.dimen.small_offset)
    private var offset = dpToPx(R.dimen.offset)
    private var pullOffset = dpToPx(R.dimen.pull_offset)

    private val sideBarWidth: Float
        get() = barWidthPixels + smallOffset

    private var paint: Paint? = null
    private var path: Path? = null

    private val gradient: LinearGradient
        get() = LinearGradient(600f, 0f, 0f, 1500f, startColorRes,
            endColorRes, Shader.TileMode.CLAMP)

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        setWillNotDraw(false)
    }

    private fun init() {
        paint = Paint().apply {
            shader = gradient
            isAntiAlias = true
        }
        path = Path()
    }

    override fun onDraw(canvas: Canvas?) {
        reset()

        if (isExpanded) {
            drawQuadBezierCurve(canvas)
        } else {
            drawCubicBezierCurve(canvas)
        }

        onStateChangeListener?.didMove(this, currentX, currentY)
    }

    private fun reset() {
        path?.reset()
    }

    private fun drawCubicBezierCurve(canvas: Canvas?) {
        path?.let {
            it.moveTo(0f, 0f)
            it.lineTo(0f, height.toFloat())
            it.lineTo(zeroX, height.toFloat())
            it.cubicTo(
                zeroX, currentY + 3 * offset,
                zeroX + currentX * invertedFraction, currentY + 3 * offset,
                zeroX + currentX * invertedFraction, currentY)
            it.cubicTo(
                zeroX + currentX * invertedFraction, currentY - 3 * offset,
                zeroX, currentY - 3 * offset,
                zeroX, 0f)
            it.lineTo(0f, 0f)
        }
        if (path != null && paint != null)
            canvas?.drawPath(path!!, paint!!)
    }

    private fun drawQuadBezierCurve(canvas: Canvas?) {
        path?.let {
            it.moveTo(0f, 0f)
            it.lineTo(0f, height.toFloat())
            it.lineTo(zeroX, height.toFloat())
            it.quadTo(controlX, height / 2f, zeroX, 0f)
            it.lineTo(0f, 0f)
        }
        if (path != null && paint != null)
            canvas?.drawPath(path!!, paint!!)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val touchOutside = isExpanded && event.x > sideBarWidth
        val touchEdge = event.x < offset && !isExpanded

        return touchEdge || touchOutside || super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isBusy || isExpanded) return true

        currentX = event.x
        currentY = event.y

        println("---------- onTouch ----------")
        println("$currentX   $currentY")

        var invalidateNeeded = false

        when (event.action) {
            ACTION_DOWN -> {
                startX = event.x
                startY = event.y

                if (event.x >= offset && !isExpanded) return false
            }
            ACTION_MOVE -> {
                invalidateNeeded = startX != currentX
                controlX = currentX.coerceAtLeast(sideBarWidth - offset)
            }
            ACTION_UP -> {
                if (!event.isClick(startX, startY)) {
                    if (!isExpanded && event.isPulled(startX, pullOffset)) {
                        expand().apply {
                            onStateChangeListener?.willExpand(this@WaveSideBar)
                        }
                    } else {
                        clearData()
                    }
                    invalidateNeeded = true
                }
            }
        }

        if (invalidateNeeded) {
            invalidate()
        }
        return true
    }

    fun collapse() {
        isBusy = true
        hideContent()
        ValueAnimator.ofFloat(sideBarWidth, 0f).apply {
            duration = collapseAnimationDuration
            addUpdateListener {
                zeroX = animatedValue as Float
            }
        }.start()
        ValueAnimator.ofFloat(sideBarWidth, 0f).apply {
            duration = collapseAnimationDuration + 100
            interpolator = BounceInterpolator()
            addUpdateListener {
                controlX = animatedValue as Float
                invalidate()
            }
            addListener(object : OnAnimationFinishedListener {
                override fun onAnimationEnd(animation: Animator?) {
                    isExpanded = false
                    clearData()
                    isBusy = false
                    onStateChangeListener?.didCollapsed(this@WaveSideBar)
                }
            })
        }.start()
    }

    fun expand() {
        isBusy = true
        ValueAnimator.ofFloat(0f, sideBarWidth).apply {
            duration = expandAnimationDuration / 2
            addUpdateListener {
                zeroX = animatedValue as Float
                invertedFraction = 1 - animatedFraction
                invalidate()
            }
            addListener(object: OnAnimationFinishedListener {
                override fun onAnimationEnd(animation: Animator?) {
                    finishExpandAnimation()
                    isExpanded = true
                    onStateChangeListener?.didExpanded(this@WaveSideBar)
                }
            })
        }.start()
        showContent()
    }

    private fun finishExpandAnimation() {
        ValueAnimator.ofFloat(currentX, sideBarWidth).apply {
            duration = 0//expandAnimationDuration / 2 + 200
            interpolator = SpringInterpolator()
            addUpdateListener {
                controlX = animatedValue as Float
                invalidate()
            }
            addListener(object : OnAnimationFinishedListener {
                override fun onAnimationEnd(animation: Animator?) {
                    isBusy = false
                }
            })
        }.start()
    }

    private fun showContent() {
//        val slide = Slide().apply {
//            slideEdge = if (orientation == Orientation.Left)
//                 Gravity.LEFT
//            else Gravity.RIGHT
//        }
//        TransitionManager.beginDelayedTransition(view as? ViewGroup, slide)

        view?.visibility = View.VISIBLE
        view?.startAnimation(
            AnimationUtils.loadAnimation(
                context, R.anim.slide_in_left
            )
        )
    }

    private fun hideContent() {
//        @AnimRes
//        val animRes: Int = if (orientation == Orientation.Left)
//            R.anim.slide_out_left
//        else
//            R.anim.slide_out_right

//        view?.startAnimation(
//            AnimationUtils.loadAnimation(
//                context, animRes
//            ).apply {
//                setAnimationListener(object: AnimationListener{
//                    override fun onAnimationEnd(anim: Animation?) {
//                        view?.visibility = View.INVISIBLE
//                    }
//                })
//            }
//        )
        view?.visibility = View.INVISIBLE
    }

    private fun clearData() {
        zeroX = 0f
        invertedFraction = 1f
        controlX = 0f
        currentX = 0f
        currentY = 0f
    }

    interface OnStateChangeListener {
        fun didMove(sender: WaveSideBar, x: Float, y: Float) = Unit

        fun willCollapse(sender: WaveSideBar) = Unit
        fun didCollapsed(sender: WaveSideBar) = Unit

        fun willExpand(sender: WaveSideBar) = Unit
        fun didExpanded(sender: WaveSideBar) = Unit
    }
    enum class Orientation {
        Left, Right
    }
}