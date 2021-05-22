package com.android.har

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.android.har.callbacks.PoseOutputListener
import com.android.har.ml.ObjectDetection
import com.android.har.models.PoseOutput
import com.android.har.utils.Utils
import com.android.har.utils.Utils.FRAME_SIZE
import com.android.har.views.PreviewWrapper
import com.android.har.views.ViewRectOverlay
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity(), PoseOutputListener {
    private val TAG = "CameraActivity"

    private val previewWrapper by lazy {
        findViewById<PreviewWrapper>(R.id.wrapper)
    }

    private val previewView by lazy {
        previewWrapper.previewView
    }

    private val labelView by lazy {
        findViewById<TextView>(R.id.action_label)
    }

    private val overlayView: ViewRectOverlay by lazy {
        previewWrapper.overlayView
    }

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraExecutor = Executors.newSingleThreadExecutor()

            val preview = Preview.Builder()
                .build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(FRAME_SIZE, FRAME_SIZE))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().apply {
                    setAnalyzer(cameraExecutor, PoseAnalyzer(this@CameraActivity))
                }

            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = getCameraSelector(cameraProvider)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exception: Exception) {
                Log.e(TAG, "startCamera: binding failed", exception)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun getCameraSelector(cameraProvider: ProcessCameraProvider): CameraSelector {
        return if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA))
            CameraSelector.DEFAULT_BACK_CAMERA
        else
            CameraSelector.DEFAULT_FRONT_CAMERA
    }

    override fun updatePoseResult(
        items: List<PoseOutput>,
        detectionResults: List<ObjectDetection.DetectionResult>
    ) {
        runOnUiThread {
            labelView.text = Utils.processList(
                items,
                sharedPreferences
            )

            previewWrapper.drawRect(detectionResults[0].locationAsRectF)
        }
    }

    fun finishCameraActivity(view: View) {
        onBackPressed()
    }

    fun openSettings(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}
