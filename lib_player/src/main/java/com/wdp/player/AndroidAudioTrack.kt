package com.wdp.player

import android.media.AudioTrack

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
class AndroidAudioTrack : IPlayer {


    private var audioTrack: AudioTrack? = null

    override fun init(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDataSource(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun play(): Boolean {
        TODO("Not yet implemented")
    }

    override fun pause(): Boolean {
        TODO("Not yet implemented")
    }

    override fun stop(): Boolean {
        TODO("Not yet implemented")
    }

    override fun release(): Boolean {
        TODO("Not yet implemented")
    }
}