package com.wdp.saver

import android.content.Context
import android.text.format.DateFormat
import java.io.File
import java.util.*

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
object SaverFactory {

    private fun getDefaultSaveFile(context: Context, fileExtName: String): String {
        val dir = context.externalCacheDir?.path
        val name = "${DateFormat.format("yyyy-MM-dd-HH-mm-ss", Date())}.$fileExtName"
        return dir + File.separator + name
    }

    fun newPcmSaver(context: Context, path: String = getDefaultSaveFile(context, "pcm")): ISaver {
        return PcmSaver(path)
    }

    fun newWavSaver(
        context: Context,
        sampleRate: Int,
        channels: Int,
        path: String = getDefaultSaveFile(context, "wav")
    ): ISaver {
        return WavSaver(path).also { it.setRecorderConfig(sampleRate, channels) }
    }
}