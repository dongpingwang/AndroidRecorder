package com.wdp.player

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
object PlayerFactory {

    fun newMediaPlayer(): IPlayer = AndroidMediaPlayer()

    fun newAudioTrack(
        sampleRateInHz: Int,
        channelConfig: Int,
        audioFormat: Int,
        bufferSizeInBytes: Int
    ): IPlayer {
        return AndroidAudioTrack(sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes)
    }
}