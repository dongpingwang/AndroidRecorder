package com.wdp.recorder

import android.annotation.SuppressLint
import android.media.AudioRecord
import android.media.AudioRecord.RECORDSTATE_RECORDING
import android.media.AudioRecord.STATE_INITIALIZED
import android.util.Log
import java.nio.ByteBuffer


/**
 * 作者：王东平
 * 功能：
 * 说明：使用AudioRecorder进行录音
 * 版本：1.0.0
 */
internal class AndroidAudioRecorder(
    private val audioSource: Int,
    private val sampleRateInHz: Int,
    private val channelConfig: Int,
    private val audioFormat: Int,
    private val bufferSizeInBytes: Int
) : IRecorder, Runnable {

    companion object {
        private const val TAG = "AndroidAudioRecorder"
        private const val NAME_THREAD_READ = "Thread-AndroidAudioRecorder"
        private const val LEN_READ = 1024 // 1KB
    }

    private var audioRecorder: AudioRecord? = null
    private val dataListeners = mutableListOf<IDataReadListener>()
    private val readBuffer by lazy { ByteBuffer.allocate(LEN_READ) }

    @SuppressLint("MissingPermission")
    override fun init(): Boolean {
        if (audioRecorder == null) {
            return kotlin.runCatching {
                AudioRecord(
                    audioSource,
                    sampleRateInHz,
                    channelConfig,
                    audioFormat,
                    bufferSizeInBytes
                )
            }.also {
                audioRecorder = it.getOrNull()
                if (audioRecorder?.state == STATE_INITIALIZED) {
                    Thread(this, NAME_THREAD_READ).start()
                }
                it.exceptionOrNull()?.printStackTrace()
            }.isSuccess
        }
        return true
    }

    override fun start(): Boolean {
        return kotlin.runCatching {
            audioRecorder?.startRecording() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    override fun stop(): Boolean {
        return kotlin.runCatching {
            audioRecorder?.stop() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    override fun release(): Boolean {
        return kotlin.runCatching {
            audioRecorder?.release() ?: throw NullPointerException()
        }.also {
            audioRecorder = null
            it.exceptionOrNull()?.printStackTrace()
        }.isSuccess
    }

    override fun addDataReadListener(dataListener: IDataReadListener): Boolean {
        synchronized(dataListener) {
            return dataListeners.contains(dataListener) || dataListeners.add(dataListener)
        }
    }

    override fun removeDataReadListener(dataListener: IDataReadListener): Boolean {
        synchronized(dataListener) {
            return dataListeners.contains(dataListener) && dataListeners.remove(dataListener)
        }
    }

    override fun run() {
        Log.d(TAG, "read run!")
        val ret = 0
        while (ret >= 0) {
            if (audioRecorder?.recordingState == RECORDSTATE_RECORDING) {
                readBuffer.clear()
                val len = kotlin.runCatching {
                    audioRecorder?.read(readBuffer.array(), 0, readBuffer.remaining())
                }.also {
                    it.exceptionOrNull()?.printStackTrace()
                }.getOrNull() ?: 0
                Log.d(TAG, "read size:$len")
                if (len > 0) {
                    val buffer = ByteArray(len)
                    readBuffer.get(buffer, 0, buffer.size)
                    synchronized(dataListeners) {
                        dataListeners.forEach {
                            it.onRead(buffer)
                        }
                    }
                }
            }
        }
    }
}