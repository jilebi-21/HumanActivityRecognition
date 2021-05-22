package com.android.har.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ViewRectOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val TAG = "ViewRectOverlay"

    private val paint: Paint = Paint().apply {
        color = Color.GREEN
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private var rect: RectF? = null

    override fun draw(canvas: Canvas?) {
        if (rect == null) return
        super.draw(canvas)

        canvas?.drawRoundRect(rect!!, 10f, 10f, paint)
    }

    fun setViewRect(rect: RectF) {
        this.rect = rect
        invalidate()
    }

}