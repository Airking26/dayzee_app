package com.dayzeeco.dayzee.androidView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.sqrt

class HexagonView : AppCompatImageView {
    private lateinit var hexagonPath: Path
    private lateinit var hexagonBorderPath: Path
    private lateinit var mBorderPaint: Paint

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        hexagonPath = Path()
        hexagonBorderPath = Path()
        mBorderPaint = Paint()
        mBorderPaint.setColor(Color.WHITE)
        mBorderPaint.setStrokeCap(Paint.Cap.ROUND)
        mBorderPaint.setStrokeWidth(0f)
        mBorderPaint.setStyle(Paint.Style.STROKE)
    }

    fun setRadius(radius: Float) {
        calculatePath(radius)
    }

    fun setBorderColor(color: Int) {
        mBorderPaint.setColor(color)
        invalidate()
    }

    private fun calculatePath(radius: Float) {
        val halfRadius = radius / 2f
        val triangleHeight = (sqrt(3.0) * halfRadius).toFloat()
        val centerX: Float = measuredWidth / 2f
        val centerY: Float = measuredHeight / 2f
        hexagonPath.reset()
        hexagonPath.moveTo(centerX, centerY + radius)
        hexagonPath.lineTo(centerX - triangleHeight, centerY + halfRadius)
        hexagonPath.lineTo(centerX - triangleHeight, centerY - halfRadius)
        hexagonPath.lineTo(centerX, centerY - radius)
        hexagonPath.lineTo(centerX + triangleHeight, centerY - halfRadius)
        hexagonPath.lineTo(centerX + triangleHeight, centerY + halfRadius)
        hexagonPath.close()
        val radiusBorder = radius - 5f
        val halfRadiusBorder = radiusBorder / 2f
        val triangleBorderHeight = (Math.sqrt(3.0) * halfRadiusBorder).toFloat()
        hexagonBorderPath.reset()
        hexagonBorderPath.moveTo(centerX, centerY + radiusBorder)
        hexagonBorderPath.lineTo(centerX - triangleBorderHeight, centerY + halfRadiusBorder)
        hexagonBorderPath.lineTo(centerX - triangleBorderHeight, centerY - halfRadiusBorder)
        hexagonBorderPath.lineTo(centerX, centerY - radiusBorder)
        hexagonBorderPath.lineTo(centerX + triangleBorderHeight, centerY - halfRadiusBorder)
        hexagonBorderPath.lineTo(centerX + triangleBorderHeight, centerY + halfRadiusBorder)
        hexagonBorderPath.close()
        invalidate()
    }

    override fun onDraw(c: Canvas) {
        c.drawPath(hexagonBorderPath, mBorderPaint)
        c.clipPath(hexagonPath, Region.Op.INTERSECT)
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        super.onDraw(c)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width: Int = MeasureSpec.getSize(widthMeasureSpec)
        val height: Int = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        calculatePath(Math.min(width / 2f, height / 2f))
    }
}