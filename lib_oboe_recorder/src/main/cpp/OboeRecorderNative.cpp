#include <jni.h>
#include <string>
#include <oboe/Oboe.h>
#include "DefaultAudioStreamDataCallback.h"

DefaultAudioStreamDataCallback audioStreamDataCallback;

extern "C" JNIEXPORT jstring
Java_com_wdp_nativelib_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    oboe::AudioStreamBuilder builder;
    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Exclusive)
            ->setSampleRate(16000)
            ->setChannelCount(oboe::ChannelCount::Stereo)
            ->setFormat(oboe::AudioFormat::I16)
            ->setDirection(oboe::Direction::Input)
            ->setDataCallback(&audioStreamDataCallback);


    return env->NewStringUTF(hello.c_str());
}


