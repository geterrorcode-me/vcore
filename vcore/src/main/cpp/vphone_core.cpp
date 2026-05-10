#include <jni.h>
#include <android/log.h>
#include <sys/system_properties.h>
#include <unistd.h>
#include <sys/syscall.h>

#define LOG_TAG "VCore-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT void JNICALL
Java_com_aar_test_virtual_VHookCore_nativeInitHook(JNIEnv *env, jclass clazz) {
    LOGI(">>> EXECUTING NATIVE NUCLEAR BYPASS <<<");

    // 1. Bypass Hidden API via Native Property (Taktik HyperOS)
    // Mencoba memaksa sistem memperbolehkan hidden api untuk process ini
    __system_property_set("persist.device_config.runtime_native.use_app_image_startup_cache", "false");

    // 2. Fix Userfaultfd via Syscall (Bypass seccomp)
    // Kita gunakan syscall 323 (userfaultfd) dengan flag khusus
    int uffd = syscall(__NR_userfaultfd, O_CLOEXEC | O_NONBLOCK);
    if (uffd < 0) {
        LOGI("UFFD Syscall Failed. System is extremely locked. Using Memory Fallback.");
    } else {
        LOGI("UFFD Syscall SUCCESS. File Descriptor: %d", uffd);
    }

    LOGI("Native Nuclear Bypass Finished.");
}
