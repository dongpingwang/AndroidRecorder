package com.wdp.saver

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.util.Log
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.Executors

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
class PcmSaver(val path: String = defaultFile) : Closeable {

    private val executor by lazy { Executors.newCachedThreadPool() }

    companion object {
        private const val TAG = "RecorderSaver"

        @SuppressLint("SdCardPath")
        private val defaultFile =
            "/mnt/sdcard/wdp_${DateFormat.format("yyyy-mm-dd-HH-mm-ss", Date())}.pcm"
    }

    private var fos: FileOutputStream? = null

    fun init() {
        Log.d(TAG, "init: $path")
        kotlin.runCatching {
            executor.execute {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                    Log.i(TAG, "delete old file")
                }
                val result = file.createNewFile()
                Log.i(TAG, "created $path --> $result")
            }
        }.exceptionOrNull()?.printStackTrace()
    }

    fun saveData(data: ByteArray) {
        kotlin.runCatching {
            executor.execute {
                if (fos == null) {
                    fos = FileOutputStream(path, true)
                }
                fos?.write(data, 0, data.size)
                fos?.flush()
            }
        }.exceptionOrNull()?.printStackTrace()
    }

    override fun close() {
        fos?.close()
    }
}