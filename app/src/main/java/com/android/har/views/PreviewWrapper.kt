package com.android.har.views

import android.content.Context
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.WindowManager
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import com.android.har.R
import com.android.har.utils.Utils

class PreviewWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.cardViewStyle
) : CardView(context, attrs, defStyleAttr) {

    val previewView = PreviewView(context)
    val overlayView = ViewRectOverlay(context)

    private val fullScreenSize: IntArray
        get() {
            val screenDimensions = IntArray(2)
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val screenSize = Point()
            windowManager.defaultDisplay.getRealSize(screenSize)
            screenDimensions[0] = screenSize.x
            screenDimensions[1] = screenSize.y
            return screenDimensions
        }

    init {
        val width = fullScreenSize[0]

        val reqHeight = width + 200

        previewView.layoutParams = LayoutParams(width, reqHeight)
        overlayView.layoutParams = LayoutParams(width, reqHeight)

        addView(previewView)
        addView(overlayView)
        radius = 40f
    }

    fun drawRect(rect: RectF) {
        val width = previewView.width
        val height = previewView.height

        rect.left = rect.left * width / Utils.FRAME_SIZE
        rect.right = rect.right * width / Utils.FRAME_SIZE
        rect.top = 50 + rect.top * height / Utils.FRAME_SIZE
        rect.bottom = rect.bottom * height / Utils.FRAME_SIZE - 50

        overlayView.setViewRect(rect)
    }
}