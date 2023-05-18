package com.wdp.oboe

import android.content.Context
import android.media.AudioManager

class OboeRecorder(
    private val audioSource: Int,
    private val sampleRateInHz: Int,
    private val channelConfig: Int,
    private val audioFormat: Int,
    private val bufferSizeInBytes: Int
) {


    companion object {
        init {
            System.loadLibrary("oboe_recorder")
            val context :Context;
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)
            devices[0].type
        }
    }

    private external fun init()
}