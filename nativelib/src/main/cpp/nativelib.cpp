#include <jni.h>
#include <string>
#include <oboe/Oboe.h>

extern "C" JNIEXPORT jstring
Java_com_wdp_nativelib_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    oboe::AudioStreamBuilder builder;
    return env->NewStringUTF(hello.c_str());
}