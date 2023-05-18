#include <jni.h>
#include <string>
#include <android/log.h>

// ----------------日志工具----------------
#define LOGD(tag, fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, tag, fmt, ##args)
#define LOGE(tag, fmt, args...) __android_log_print(ANDROID_LOG_ERROR, tag, fmt, ##args)



// ----------------异常----------------
static void jniThrowException(JNIEnv *env, const char *exception, const char *msg) {
    jclass clazz = env->FindClass(exception);
    if (clazz != nullptr) {
        env->ThrowNew(clazz, msg);
    }
    env->DeleteLocalRef(clazz);
}
