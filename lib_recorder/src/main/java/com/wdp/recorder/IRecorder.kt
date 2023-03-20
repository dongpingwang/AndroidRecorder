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

    fun addDataReadListener(dataListener: IDataReadListener): Boolean

    fun removeDataReadListener(dataListener: IDataReadListener): Boolean
}

interface IDataReadListener {
    fun onRead(data: ByteArray)
}
