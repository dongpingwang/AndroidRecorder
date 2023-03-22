package com.wdp.player

import android.media.AudioManager
import android.media.AudioTrack
import android.media.AudioTrack.MODE_STREAM
import android.media.AudioTrack.PLAYSTATE_PLAYING
import android.util.Log
import java.io.FileInputStream
import java.nio.ByteBuffer

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
class AndroidAudioTrack(
    private val sampleRateInHz: Int,
    private val channelConfig: Int,
    private val audioFormat: Int,
    private val bufferSizeInBytes: Int
) : IPlayer, Runnable {

    companion object {
        private const val TAG = "AndroidAudioTrack"
        private const val NAME_THREAD_WRITE = "Thread-AndroidAudioTrack"
        private const val LEN_READ = 1024 // 1KB
    }

    private var player: AudioTrack? = null
    private val readBuffer by lazy { ByteBuffer.allocate(LEN_READ) }
    private var fis: FileInputStream? = null
    override fun init(): Boolean {
        if (player == null) {
            return kotlin.runCatching {
                AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRateInHz,
                    channelConfig,
                    audioFormat,
                    bufferSizeInBytes,
                    MODE_STREAM
                )
            }.also {
                player = it.getOrNull()
                if (player?.state == AudioTrack.STATE_INITIALIZED) {
                    Thread(this, NAME_THREAD_WRITE).start()
                }
                it.exceptionOrNull()?.printStackTrace()
            }.isSuccess
        }
        return true
    }

    override fun setDataSource(path: String): Boolean {
        if (path.isEmpty()) {
            throw java.lang.IllegalArgumentException("path can't be empty!")
        }
        return kotlin.runCatching {
            fis?.close()
            fis = null
            fis = FileInputStream(path)
        }.also {
            it.exceptionOrNull()?.printStackTrace()
        }.isSuccess
    }

    override fun play(): Boolean {
        return kotlin.runCatching {
            player?.play() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    override fun pause(): Boolean {
        return kotlin.runCatching {
            player?.pause() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    override fun stop(): Boolean {
        return kotlin.runCatching {
            player?.stop() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    override fun release(): Boolean {
        return kotlin.runCatching {
            player?.release() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    override fun run() {
        Log.d(TAG, "read run!")
        var ret = 0
        while (ret >= 0) {
            if (player?.playState == PLAYSTATE_PLAYING && fis != null) {
                kotlin.runCatching {
                    readBuffer.clear()
                    val len = fis?.read(readBuffer.array(), 0, readBuffer.remaining()) ?: 0
                    Log.d(TAG, "read size:$len")
                    if (len > 0) {
                        val buffer = ByteArray(len)
                        readBuffer.get(buffer, 0, buffer.size)
                        player?.write(buffer, 0, buffer.size)
                    }
                }
            }
        }
    }
}