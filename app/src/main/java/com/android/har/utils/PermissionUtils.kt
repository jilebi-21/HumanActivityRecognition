package com.android.har.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val ALL_PERMISSIONS_REQUEST_CODE = 50
    const val STORAGE_PERMISSION_REQUEST_CODE = 51
    const val CAMERA_PERMISSION_REQUEST_CODE = 52

    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    fun isPermissionGranted(context: Context, perm: String): Boolean {
        val result = ContextCompat.checkSelfPermission(context, perm)
        return result == PackageManager.PERMISSION_GRANTED
    }
}