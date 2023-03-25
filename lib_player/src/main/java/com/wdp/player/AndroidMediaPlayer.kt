package com.wdp.player

import android.media.MediaPlayer
import android.util.Log

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
class AndroidMediaPlayer : IPlayer, MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    companion object {
        private const val TAG = "AndroidMediaPlayer"
    }

    private var player: MediaPlayer? = null
    private var state = State.UNINITIALIZED

    private var autoPlayAfterPrepared = true

    enum class State(desc: String) {
        INITIALIZED("INITIALIZED"),
        PREPARED("PREPARED"),
        UNINITIALIZED("UNINITIALIZED")
    }

    override fun init(): Boolean {
        if (player == null) {
            player = MediaPlayer().also {
                updateState(State.INITIALIZED)
                it.setOnPreparedListener(this)
                it.setOnInfoListener(this)
                it.setOnCompletionListener(this)
                it.setOnErrorListener(this)
            }
        }
        return true
    }

    override fun setDataSource(path: String): Boolean {
        return kotlin.runCatching {
            player?.let {
                Log.d(TAG, "setDataSource: $path")
                it.setDataSource(path)
                it.prepareAsync()
            } ?: throw NullPointerException()
        }.isSuccess
    }

    override fun play(): Boolean {
        return kotlin.runCatching {
            if (state == State.PREPARED) {
                player?.start() ?: throw NullPointerException()
            } else {
                throw java.lang.IllegalArgumentException()
            }
        }.also {
            it.exceptionOrNull()?.printStackTrace()
        }.isSuccess
    }

    override fun pause(): Boolean {
        return kotlin.runCatching {
            player?.pause() ?: throw NullPointerException()
        }.isSuccess
    }

    override fun stop(): Boolean {
        return kotlin.runCatching {
            player?.stop() ?: throw NullPointerException()
        }.isSuccess
    }

    override fun release(): Boolean {
        return kotlin.runCatching {
            player?.let {
                it.release()
                player = null
                updateState(State.UNINITIALIZED)
            } ?: throw NullPointerException()
        }.isSuccess
    }

    override fun registerOnPlayerCompleteListener(listener: OnPlayerCompleteListener) = false
    override fun unregisterOnPlayerCompleteListener(listener: OnPlayerCompleteListener) = false

    override fun onPrepared(mp: MediaPlayer?) {
        Log.d(TAG, ">> onPrepared")
        updateState(State.PREPARED)
        if (autoPlayAfterPrepared) {
            play()
        }
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, ">> onInfo:[what:$what,extra:$extra]")
        return true
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG, ">> onInfo")
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, ">> onError:[what:$what,extra:$extra]")
        return true
    }

    private fun updateState(newState: State) {
        Log.d(TAG, "state: $state ==> $newState")
        state = newState
    }
}