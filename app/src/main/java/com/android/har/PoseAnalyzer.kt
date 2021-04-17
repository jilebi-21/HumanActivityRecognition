package com.android.har

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.android.har.callbacks.PoseOutputListener
import com.android.har.ml.PoseModel
import com.android.har.models.PoseOutput
import com.android.har.utils.YuvToRgbConverter
import org.tensorflow.lite.support.image.TensorImage

class PoseAnalyzer(
    val activity: Activity,
) : ImageAnalysis.Analyzer {
    private val TAG = "PoseAnalyzer"

    private val converter: YuvToRgbConverter
    private lateinit var bitmapBuffer: Bitmap
    private lateinit var rotationMatrix: Matrix

    private var mListener: PoseOutputListener? = null

    init {
        Log.i(TAG, "Initializing PoseAnalyzer.")
        converter = YuvToRgbConverter(activity)

        if (activity is PoseOutputListener) {
            mListener = activity
        }
    }

    override fun analyze(imageProxy: ImageProxy) {
        val items = mutableListOf<PoseOutput>()

        val model = PoseModel.newInstance(activity)
        val tfLite = TensorImage.fromBitmap(toBitmap(imageProxy))
        val outputs = model.process(tfLite)
            .probabilityAsCategoryList

        for (output in outputs) {
            items.add(PoseOutput(output.label, output.score))
        }

        mListener?.updatePoseResult(items)

        model.close()
        imageProxy.close()
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun toBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null

        if (!::bitmapBuffer.isInitialized) {
            Log.d(TAG, "Initialize toBitmap()")
            rotationMatrix = Matrix()
            rotationMatrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            bitmapBuffer = Bitmap.createBitmap(
                imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
            )
        }

        converter.yuvToRgb(image, bitmapBuffer)

        return Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            rotationMatrix,
            false
        )
    }
}
