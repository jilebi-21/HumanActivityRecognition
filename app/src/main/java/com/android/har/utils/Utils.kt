package com.android.har.utils

import android.content.SharedPreferences
import com.android.har.models.PoseOutput

object Utils {
    const val KEY_ASPECT_RATIO = "aspect_ratio"
    const val ASPECT_1_1 = "aspect_1_1"
    const val ASPECT_4_3 = "aspect_4_3"
    const val ASPECT_16_9 = "aspect_16_9"
    const val ASPECT_FULL = "aspect_full"

    const val KEY_DISPLAY_SCORE = "display_score"
    const val FRAME_SIZE = 224

    fun processList(items: List<PoseOutput>, sharedPreferences: SharedPreferences): String {
        val displayScore = sharedPreferences.getBoolean(KEY_DISPLAY_SCORE, false)

        val topResult = items.maxByOrNull {
            it.probability
        } ?: return ""

        val sb = StringBuilder(topResult.label)

        if (displayScore) {
            sb.append(" : ")
                .append(String.format("%.02f", topResult.probability))
        }

        return sb.toString()
    }
}