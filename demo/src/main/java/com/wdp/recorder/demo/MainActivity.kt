package com.wdp.recorder.demo

import android.media.AudioFormat.CHANNEL_IN_DEFAULT
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.media.MediaRecorder
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
import com.wdp.recorder.IDataReadListener
import com.wdp.recorder.RecorderFactory
import com.wdp.saver.PcmSaver
import com.wdp.saver.getDefaultSaveFile
import java.io.File
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
    private val channelConfig = CHANNEL_IN_DEFAULT
    private val audioFormat = ENCODING_PCM_16BIT
    private val bufferSizeInBytes =
        AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
    private val recorder by lazy {
        RecorderFactory.newAudioRecorder(
            audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes
        )
    }
    private val saver by lazy { PcmSaver(this) }

    private val outputPath by lazy { saver.path }

    // -------------------MediaRecorder-------------------
//    private val outputPath by lazy { getDefaultSaveFile(this, "amr") }
//    private val audioSource = VOICE_RECOGNITION
//    private val sampleRateInHz = 16000
//    private val channelConfig = 1
//    private val audioFormat = MediaRecorder.OutputFormat.AMR_NB
//    private val audioEncoder = MediaRecorder.AudioEncoder.AMR_NB
//    private val recorder by lazy {
//        Log.d(TAG, "outputPath: $outputPath")
//        RecorderFactory.newMediaRecorder(
//            audioSource, sampleRateInHz, channelConfig, audioFormat, audioEncoder, outputPath
//        )
//    }

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

        recorder.addDataReadListener(object : IDataReadListener {
            override fun onRead(data: ByteArray) {
                saver.saveData(data)
            }
        })

        saver.init()
        tvFilePath.text = outputPath
        btnStartRecorder.setOnClickListener(this)
        btnStopRecorder.setOnClickListener(this)
        btnPlay.setOnClickListener(this)

        // todo 耗时
        tvRecorderState.text = if (recorder.init()) "已初始化" else "初始化失败"
    }

    override fun onClick(v: View) {
        if (v == btnStartRecorder) {
            // todo 耗时
            tvRecorderState.text = if (recorder.start()) "录音中" else "开始录音失败"
        } else if (v == btnStopRecorder) {
            // todo 耗时
            tvRecorderState.text = if (recorder.stop()) "结束录音" else "结束录音失败"
        } else if (v == btnPlay) {
            player.init()
            player.setDataSource(outputPath)
            player.play()
        }
    }
}