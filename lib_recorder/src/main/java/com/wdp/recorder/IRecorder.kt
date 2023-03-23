package com.wdp.recorder

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
interface IRecorder {

    fun init(): Boolean

    fun start(): Boolean

    fun stop(): Boolean

    fun pause(): Boolean
    fun resume(): Boolean

    fun release(): Boolean

    fun addOnDataReadListener(dataListener: OnDataReadListener): Boolean

    fun removeOnDataReadListener(dataListener: OnDataReadListener): Boolean
}

interface OnDataReadListener {
    fun onRead(data: ByteArray)
}
