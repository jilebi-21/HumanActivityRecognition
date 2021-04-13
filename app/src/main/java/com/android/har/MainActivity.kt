package com.android.har

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.har.utils.PermissionUtils

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openFileViewer(view: View) {
        if (!PermissionUtils.isPermissionGranted(this, READ_EXTERNAL_STORAGE)) {
            requestPermissions(
                arrayOf(READ_EXTERNAL_STORAGE),
                PermissionUtils.STORAGE_PERMISSION_REQUEST_CODE
            )
            return
        }
        //TODO: open file viewer to select a video
    }

    fun openCamera(view: View) {
        if (!PermissionUtils.isPermissionGranted(this, CAMERA)) {
            requestPermissions(
                arrayOf(CAMERA),
                PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE
            )
            return
        }
        //TODO: open camera activity
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE ||
            requestCode == PermissionUtils.STORAGE_PERMISSION_REQUEST_CODE
        ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "${permissions[0]} granted.")
            } else {
                Log.e(TAG, "${permissions[0]} denied.")
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}