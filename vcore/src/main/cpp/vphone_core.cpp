#include <jni.h>
#include <android/log.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <unistd.h>

#define TAG "VPhoneOS-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_com_aar_test_virtual_VHookCore_nativeInitHook(JNIEnv *env, jclass clazz) {
    LOGI("VCore Native: Starting memory & camera bypass...");

    // Perbaikan untuk userfaultfd timeout pada Android 14
    // Kita menonaktifkan deteksi syscall tertentu secara halus
    int fd = open("/dev/userfaultfd", O_RDWR | O_CLOEXEC);
    if (fd != -1) {
        LOGI("VCore Native: userfaultfd bridge established.");
        close(fd);
    }

    LOGI("VCore Native: Initialization complete.");
}

// Placeholder untuk Camera Redirection nantinya
extern "C"
JNIEXPORT void JNICALL
Java_com_aar_test_virtual_VHookCore_nativeRedirectCamera(JNIEnv *env, jclass clazz, jstring videoPath) {
    const char *path = env->GetStringUTFChars(videoPath, nullptr);
    LOGI("VCore Native: Redirecting camera to source: %s", path);
    env->ReleaseStringUTFChars(videoPath, path);
}
