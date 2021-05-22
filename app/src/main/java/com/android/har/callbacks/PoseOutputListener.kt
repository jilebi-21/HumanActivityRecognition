package com.android.har.callbacks

import com.android.har.ml.ObjectDetection
import com.android.har.models.PoseOutput

interface PoseOutputListener {
    fun updatePoseResult(
        items: List<PoseOutput>,
        detectionResults: List<ObjectDetection.DetectionResult>
    )
}