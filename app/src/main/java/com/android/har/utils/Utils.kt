package com.android.har.utils

import android.content.SharedPreferences
import com.android.har.models.PoseOutput

object Utils {
    const val KEY_DISPLAY_MORE_RESULTS = "display_top_results"
    const val KEY_DISPLAY_PROBABILITY = "display_probability"
    const val KEY_DISPLAY_FPS = "display_fps"

    fun processList(items: List<PoseOutput>, sharedPreferences: SharedPreferences): String {
        val displayMore = sharedPreferences.getBoolean(KEY_DISPLAY_MORE_RESULTS, false)
        val displayProbability = sharedPreferences.getBoolean(KEY_DISPLAY_PROBABILITY, false)
        val displayFps = sharedPreferences.getBoolean(KEY_DISPLAY_FPS, false)

        val result = items.sortedByDescending { it.probability }
            .take(if (displayMore) 3 else 1)

        val sb = StringBuilder()
        for (item in result) {
            sb.append(item.label)
            if (displayProbability) {
                sb.append(" : ")
                    .append(item.probability)
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}