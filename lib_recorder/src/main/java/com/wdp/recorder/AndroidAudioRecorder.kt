package com.wdp.recorder

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.RECORDSTATE_RECORDING
import android.media.AudioRecord.STATE_INITIALIZED
import android.media.MediaRecorder
import android.util.Log
import java.nio.ByteBuffer


/**
 * 作者：王东平
 * 功能：录音
 * 说明：使用AudioRecorder进行录音
 * 版本：1.0.0
 */
internal class AndroidAudioRecorder(
//    private val audioSource: Int,
//    private val sampleRateInHz: Int,
//    private val channelConfig: Int,
//    private val audioFormat: Int,
//    private val bufferSizeInBytes: Int
) : IRecorder, Runnable {

    companion object {
        private const val TAG = "AndroidAudioRecorder"
        private const val NAME_THREAD_READ = "Thread-AndroidAudioRecorder"
        private const val LEN_READ = 1024 // 1KB

        val ECHO_REFERENCE = 1997
        val VOICE_RECOGNITION = MediaRecorder.AudioSource.VOICE_RECOGNITION
    }

    private var recorder: AudioRecord? = null
    private val dataListeners = mutableListOf<OnDataReadListener>()
    private val readBuffer by lazy { ByteBuffer.allocate(LEN_READ) }
    private val audioSource = ECHO_REFERENCE
    private val sampleRateInHz = 16000
    private val channelIndexMask = getChannelMask(4)
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSizeInBytes = AudioRecord.getMinBufferSize(
        sampleRateInHz,
        AudioFormat.CHANNEL_IN_STEREO,
        audioFormat
    ) * 10

    private fun getChannelMask(channelCnt: Int): Int {
        return 1.shl(channelCnt) - 1
    }


    @SuppressLint("MissingPermission")
    override fun init(): Boolean {
        if (recorder == null) {
            return kotlin.runCatching {
                val builder = AudioRecord.Builder()
                val setAudioAttributes = builder.javaClass.getDeclaredMethod(
                    "setAudioAttributes",
                    AudioAttributes::class.java
                )
                val audioAttributes = AudioAttributes.Builder()
                val setInternalCapturePreset = audioAttributes.javaClass.getDeclaredMethod("setInternalCapturePreset", Int::class.java)
                setInternalCapturePreset.invoke(audioAttributes, audioSource)

                setAudioAttributes.invoke(builder, audioAttributes.build())

                builder.setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRateInHz)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelIndexMask(channelIndexMask)
                        .build()
                )
                    .setBufferSizeInBytes(bufferSizeInBytes)
                    .build()
            }.also {
                recorder = it.getOrNull()
                if (recorder?.state == STATE_INITIALIZED) {
                    Thread(this, NAME_THREAD_READ).start()
                }
                it.exceptionOrNull()?.printStackTrace()
            }.isSuccess
        }
        return true
    }


    override fun start(): Boolean {
        return kotlin.runCatching {
            recorder?.startRecording() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    override fun stop(): Boolean {
        return kotlin.runCatching {
            recorder?.stop() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    override fun pause(): Boolean = stop()
    override fun resume(): Boolean = start()
    override fun release(): Boolean {
        return kotlin.runCatching {
            recorder?.release() ?: throw NullPointerException()
        }.also {
            recorder = null
            it.exceptionOrNull()?.printStackTrace()
        }.isSuccess
    }

    override fun addOnDataReadListener(dataListener: OnDataReadListener): Boolean {
        synchronized(dataListener) {
            return dataListeners.contains(dataListener) || dataListeners.add(dataListener)
        }
    }

    override fun removeOnDataReadListener(dataListener: OnDataReadListener): Boolean {
        synchronized(dataListener) {
            return dataListeners.contains(dataListener) && dataListeners.remove(dataListener)
        }
    }

    override fun run() {
        Log.d(TAG, "read run!")
        val ret = 0
        while (ret >= 0) {
            if (recorder?.recordingState == RECORDSTATE_RECORDING) {
                readBuffer.clear()
                val len = kotlin.runCatching {
                    recorder?.read(readBuffer.array(), 0, readBuffer.remaining())
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