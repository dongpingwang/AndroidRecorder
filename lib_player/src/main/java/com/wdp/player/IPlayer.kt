package com.wdp.player

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
interface IPlayer {

    fun init(): Boolean

    fun setDataSource(path: String): Boolean

    fun play(): Boolean

    fun pause(): Boolean

    fun stop(): Boolean

    fun release(): Boolean

    fun registerOnPlayerCompleteListener(listener: OnPlayerCompleteListener): Boolean
    fun unregisterOnPlayerCompleteListener(listener: OnPlayerCompleteListener): Boolean
}

interface OnPlayerCompleteListener {
    fun onComplete()
}