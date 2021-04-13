package com.android.har

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openFileViewer(view: View) {
        //TODO: open file viewer to select a video
    }

    fun openCamera(view: View) {
        //TODO: open camera activity
    }
}