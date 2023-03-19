package com.wdp.recorder

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
object RecorderFactory {

    fun newAudioRecorder(
        audioSource: Int,
        sampleRateInHz: Int,
        channelConfig: Int,
        audioFormat: Int,
        bufferSizeInBytes: Int
    ): IRecorder {
        return AndroidAudioRecorder(
            audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes
        )
    }

    fun newMediaRecorder(): IRecorder = TODO()
}