package com.android.har.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val CAMERA_PERMISSION_REQUEST_CODE = 52

    fun isPermissionGranted(context: Context, perm: String): Boolean {
        val result = ContextCompat.checkSelfPermission(context, perm)
        return result != PackageManager.PERMISSION_DENIED
    }
}