package com.wdp.saver

import java.io.Closeable

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
interface ISaver : Closeable {

    fun init()

    fun saveData(data: ByteArray)

    override fun close()

    fun getPath():String
}