package com.wdp.recorder

import android.media.MediaRecorder
import android.util.Log

/**
 * 作者：王东平
 * 功能：录音
 * 说明：使用MediaRecorder进行录音
 * 版本：1.0.0
 */
internal class AndroidMediaRecorder(
    private val audioSource: Int,
    private val sampleRateInHz: Int,
    private val channelConfig: Int,
    private val audioFormat: Int,
    private val audioEncoder: Int,
    private val outputPath: String,
) : IRecorder, MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener {
    companion object {
        private const val TAG = "AndroidMediaRecorder"
    }

    private var recorder: MediaRecorder? = null
    override fun init(): Boolean {
        if (recorder == null) {
            return kotlin.runCatching {
                recorder = MediaRecorder().apply {
                    setAudioSource(audioSource)
                    setAudioSamplingRate(sampleRateInHz)
                    setAudioChannels(channelConfig)
                    setOutputFormat(audioFormat)
                    setAudioEncoder(audioEncoder)
                    setOutputFile(outputPath)
                    setOnErrorListener(this@AndroidMediaRecorder)
                    setOnInfoListener(this@AndroidMediaRecorder)
                }
                recorder?.prepare()
            }.also {
                it.exceptionOrNull()?.printStackTrace()
            }.isSuccess
        }
        return true
    }

    override fun start(): Boolean {
        return init() && kotlin.runCatching {
            recorder?.start() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    override fun stop(): Boolean {
        return kotlin.runCatching {
            recorder?.stop() ?: throw NullPointerException()
            recorder?.release() ?: throw NullPointerException()
        }.also {
            recorder = null
            it.exceptionOrNull()?.printStackTrace()
        }.isSuccess
    }

    override fun pause(): Boolean {
        return init() && kotlin.runCatching {
            recorder?.pause() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    override fun resume(): Boolean {
        return init() && kotlin.runCatching {
            recorder?.resume() ?: throw NullPointerException()
        }.also { it.exceptionOrNull()?.printStackTrace() }.isSuccess || start()
    }

    override fun release(): Boolean {
        return stop()
    }

    override fun addOnDataReadListener(dataListener: OnDataReadListener): Boolean = false
    override fun removeOnDataReadListener(dataListener: OnDataReadListener): Boolean = false
    override fun onError(mr: MediaRecorder?, what: Int, extra: Int) {
        Log.d(TAG, ">> onError:[what=$what, extra=$extra]")
    }

    override fun onInfo(mr: MediaRecorder?, what: Int, extra: Int) {
        Log.d(TAG, ">> onInfo:[what=$what, extra=$extra]")
    }
}