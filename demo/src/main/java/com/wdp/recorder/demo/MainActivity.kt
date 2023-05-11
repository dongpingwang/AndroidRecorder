package com.wdp.recorder.demo

import android.media.AudioFormat
import android.media.AudioFormat.CHANNEL_IN_DEFAULT
import android.media.AudioFormat.CHANNEL_IN_STEREO
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource.VOICE_RECOGNITION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wdp.player.PlayerFactory
import com.wdp.recorder.OnDataReadListener
import com.wdp.recorder.RecorderFactory
import com.wdp.saver.SaverFactory
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var btnStartRecorder: Button
    private lateinit var btnStopRecorder: Button
    private lateinit var btnPlay: Button
    private lateinit var tvRecorderState: TextView
    private lateinit var tvFilePath: TextView

    // -------------------AudioRecorder-------------------
    private val audioSource = VOICE_RECOGNITION
    private val sampleRateInHz = 16000
    private val channelConfig = 4
    private val audioFormat = ENCODING_PCM_16BIT
    private val bufferSizeInBytes =
        AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
    private val recorder by lazy {
        RecorderFactory.newAudioRecorder(
            audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes
        )
    }

    private val saver by lazy { SaverFactory.newPcmSaver(this) }
    private val outputPath by lazy { saver.getPath() }

    private val uiHandler by lazy { Handler(Looper.getMainLooper()) }
    private val executor by lazy { Executors.newCachedThreadPool() }

    private val player by lazy {
        PlayerFactory.newAudioTrack(
            sampleRateInHz,
            channelConfig,
            audioFormat,
            bufferSizeInBytes
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStartRecorder = findViewById(R.id.btn_start_recorder)
        btnStopRecorder = findViewById(R.id.btn_stop_recorder)
        btnPlay = findViewById(R.id.btn_play)
        tvRecorderState = findViewById(R.id.tv_recorder_state)
        tvFilePath = findViewById(R.id.tv_recorder_path)

        recorder.addOnDataReadListener(object : OnDataReadListener {
            override fun onRead(data: ByteArray) {
                saver.saveData(data)
            }
        })

        saver.init()
        tvFilePath.text = outputPath
        btnStartRecorder.setOnClickListener(this)
        btnStopRecorder.setOnClickListener(this)
        btnPlay.setOnClickListener(this)

        executor.execute {
            recorder.init().also {
                uiHandler.post {
                    tvRecorderState.text = if (it) "已初始化" else "初始化失败"
                }
            }
        }
    }

    override fun onClick(v: View) {
        if (v == btnStartRecorder) {
            executor.execute {
                recorder.start().also {
                    uiHandler.post {
                        tvRecorderState.text = if (it) "录音中" else "开始录音失败"
                    }
                }
            }
        } else if (v == btnStopRecorder) {
            executor.execute {
                recorder.stop().also {
                    uiHandler.post {
                        tvRecorderState.text = if (it) "结束录音" else "结束录音失败"
                    }
                }
            }
        } else if (v == btnPlay) {
            executor.execute {
                player.init()
                player.setDataSource(outputPath)
                player.play()
            }
        }
    }
}