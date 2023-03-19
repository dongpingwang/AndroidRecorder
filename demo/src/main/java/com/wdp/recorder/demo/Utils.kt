package com.wdp.recorder.demo

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
object Utils {

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            ),
            10
        )
    }
}