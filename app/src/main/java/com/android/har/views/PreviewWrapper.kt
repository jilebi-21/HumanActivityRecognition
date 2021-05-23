package com.android.har.views

import android.content.Context
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.view.WindowManager
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.preference.PreferenceManager
import com.android.har.R
import com.android.har.utils.Utils
import com.android.har.utils.Utils.ASPECT_16_9
import com.android.har.utils.Utils.ASPECT_1_1
import com.android.har.utils.Utils.ASPECT_4_3
import com.android.har.utils.Utils.ASPECT_FULL
import com.android.har.utils.Utils.KEY_ASPECT_RATIO

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
        val screenWidth = fullScreenSize[0]
        val screenHeight = fullScreenSize[1]

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val ratio = sharedPrefs.getString(KEY_ASPECT_RATIO, ASPECT_1_1)

        val reqHeight =
            when (ratio) {
                ASPECT_4_3 -> screenWidth * 4 / 3
                ASPECT_16_9 -> screenWidth * 16 / 9
                ASPECT_FULL -> screenHeight
                else -> screenWidth
            }

        previewView.layoutParams = LayoutParams(screenWidth, reqHeight)
        overlayView.layoutParams = LayoutParams(screenWidth, reqHeight)

        addView(previewView)
        addView(overlayView)
        radius = 40f
    }

    fun drawRect(rect: RectF) {
        val width = previewView.width
        val height = previewView.height

        rect.left = rect.left * width / Utils.FRAME_SIZE
        rect.right = rect.right * width / Utils.FRAME_SIZE
        rect.top = rect.top * height / Utils.FRAME_SIZE
        rect.bottom = rect.bottom * height / Utils.FRAME_SIZE

        overlayView.setViewRect(rect)
    }
}