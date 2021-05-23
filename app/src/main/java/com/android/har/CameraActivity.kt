package com.android.har

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.android.har.callbacks.PoseOutputListener
import com.android.har.databinding.ActivityCameraBinding
import com.android.har.ml.ObjectDetection
import com.android.har.models.PoseOutput
import com.android.har.utils.PermissionUtils
import com.android.har.utils.PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE
import com.android.har.utils.Utils
import com.android.har.utils.Utils.ASPECT_16_9
import com.android.har.utils.Utils.ASPECT_1_1
import com.android.har.utils.Utils.ASPECT_4_3
import com.android.har.utils.Utils.ASPECT_FULL
import com.android.har.utils.Utils.FRAME_SIZE
import com.android.har.utils.Utils.KEY_ASPECT_RATIO
import com.android.har.utils.Utils.KEY_DISPLAY_SCORE
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity(), PoseOutputListener,
    ChipGroup.OnCheckedChangeListener {
    private val TAG = "CameraActivity"

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private lateinit var binding: ActivityCameraBinding

    private val bottomSheetBehavior: BottomSheetBehavior<View> by lazy {
        BottomSheetBehavior.from(binding.bottomView)
    }

    private val chipGroup: ChipGroup by lazy { findViewById(R.id.chip_group) }
    private val aspect_1_1: Chip by lazy { findViewById(R.id.aspect_1_1) }
    private val aspect_4_3: Chip by lazy { findViewById(R.id.aspect_4_3) }
    private val aspect_16_9: Chip by lazy { findViewById(R.id.aspect_16_9) }
    private val aspect_full: Chip by lazy { findViewById(R.id.aspect_full) }

    private val scorePref: SwitchCompat by lazy {
        findViewById(R.id.score_switch)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cameraPerm = PermissionUtils.isPermissionGranted(this, Manifest.permission.CAMERA)
        if (!cameraPerm) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        binding.threeDotMenu.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val callback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomSheet.alpha = slideOffset
            }
        }
        bottomSheetBehavior.addBottomSheetCallback(callback)
        callback.onSlide(binding.bottomView, 0f)

        when (sharedPreferences.getString(KEY_ASPECT_RATIO, ASPECT_1_1)) {
            ASPECT_1_1 -> chipGroup.check(aspect_1_1.id)
            ASPECT_4_3 -> chipGroup.check(aspect_4_3.id)
            ASPECT_16_9 -> chipGroup.check(aspect_16_9.id)
            ASPECT_FULL -> chipGroup.check(aspect_full.id)
        }

        chipGroup.setOnCheckedChangeListener(this)

        scorePref.setOnCheckedChangeListener { _, checked ->
            sharedPreferences.edit().putBoolean(KEY_DISPLAY_SCORE, checked).apply()
        }

        startCamera()
    }

    override fun onCheckedChanged(group: ChipGroup?, checkedId: Int) {
        val key: String = when (checkedId) {
            aspect_4_3.id -> ASPECT_4_3
            aspect_16_9.id -> ASPECT_16_9
            aspect_full.id -> ASPECT_FULL
            else -> ASPECT_1_1
        }
        sharedPreferences.edit().putString(KEY_ASPECT_RATIO, key).apply()
        recreate()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraExecutor = Executors.newSingleThreadExecutor()

            val preview = Preview.Builder()
                .build().apply {
                    setSurfaceProvider(binding.wrapper.previewView.surfaceProvider)
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
            val result = detectionResults[0]
            val category = result.categoryAsString

            val rect = if (category.equals("person", true)) {
                binding.actionLabel.text = Utils.processList(items, sharedPreferences)
                detectionResults[0].locationAsRectF
            } else {
                binding.actionLabel.text = ""
                RectF()
            }
            binding.wrapper.drawRect(rect)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "${permissions[0]} denied.")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun finishCameraActivity(view: View) {
        onBackPressed()
    }
}
