package com.android.har.callbacks

import com.android.har.models.PoseOutput

interface PoseOutputListener {
    fun updatePoseResult(items: List<PoseOutput>)
}