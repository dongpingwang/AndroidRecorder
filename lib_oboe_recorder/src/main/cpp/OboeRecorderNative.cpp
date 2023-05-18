#include <jni.h>
#include <string>
#include <oboe/Oboe.h>
#include "DefaultAudioStreamDataCallback.h"
#include "utils.h"

const char *TAG = "OboeRecorderJNI";

DefaultAudioStreamDataCallback audioStreamDataCallback;
std::shared_ptr<oboe::AudioStream> stream;

extern "C"
JNIEXPORT void JNICALL
Java_com_wdp_oboe_OboeRecorder_init(JNIEnv *env, jobject thiz) {
    oboe::AudioStreamBuilder builder;
    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Exclusive)
            ->setSampleRate(16000)
            ->setChannelCount(oboe::ChannelCount::Stereo)
            ->setFormat(oboe::AudioFormat::I16)
            ->setDirection(oboe::Direction::Input)
//            ->setDeviceId()
            ->setDataCallback(&audioStreamDataCallback);

    oboe::Result result = builder.openStream(stream);
    LOGD(TAG, "openStream, %s", oboe::convertToText(result));
    if (result == oboe::Result::OK) {
        stream
    }
}