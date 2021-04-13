package com.android.har

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.har.utils.PermissionUtils

class LaunchActivity : AppCompatActivity() {
    val TAG = "LaunchActivity"

    val APP_PREFERENCE_NAME = "app_prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSharedPreferences("app_prefs", MODE_PRIVATE).apply {
            getBoolean("is_initial", true).let {
                if (it) {
                    requestPermissions(
                        PermissionUtils.REQUIRED_PERMISSIONS,
                        PermissionUtils.ALL_PERMISSIONS_REQUEST_CODE
                    )
                    edit().putBoolean("is_initial", false).apply()
                } else {
                    startMainActivity()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtils.ALL_PERMISSIONS_REQUEST_CODE -> {
                for (i in permissions.indices) {
                    if (grantResults[permissions.indexOf(PermissionUtils.REQUIRED_PERMISSIONS[i])] == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "${permissions[i]} granted.")
                    } else {
                        Log.e(TAG, "${permissions[i]} denied.")
                    }
                }
                startMainActivity()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}