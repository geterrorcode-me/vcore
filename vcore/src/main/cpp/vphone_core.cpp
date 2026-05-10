#include <jni.h>
#include <android/log.h>
#include <sys/system_properties.h>
#include <unistd.h>
#include <sys/syscall.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <string.h> // WAJIB: Untuk fungsi strerror

// Memastikan __NR_userfaultfd tersedia untuk ARM64
#ifndef __NR_userfaultfd
  #if defined(__aarch64__)
    #define __NR_userfaultfd 323
  #endif
#endif

#define LOG_TAG "VCore-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT void JNICALL
Java_com_aar_test_virtual_VHookCore_nativeInitHook(JNIEnv *env, jclass clazz) {
    LOGI(">>> EXECUTING NATIVE NUCLEAR BYPASS <<<");

    // 1. Bypass Hidden API via Native Property (Taktik HyperOS)
    __system_property_set("persist.device_config.runtime_native.use_app_image_startup_cache", "false");

    // 2. Fix Userfaultfd via Syscall
    // Menggunakan flag O_CLOEXEC dan O_NONBLOCK untuk handshake awal
    long uffd = syscall(__NR_userfaultfd, O_CLOEXEC | O_NONBLOCK);
    
    if (uffd < 0) {
        // Sekarang strerror(errno) sudah bisa terbaca
        LOGI("UFFD Syscall Failed: %s (errno: %d)", strerror(errno), errno);
    } else {
        LOGI("UFFD Syscall SUCCESS. File Descriptor: %ld", uffd);
        // Biarkan FD tetap terbuka agar tidak di-reclaim oleh sistem segera
    }

    LOGI("Native Nuclear Bypass Finished.");
}

extern "C" JNIEXPORT void JNICALL
Java_com_aar_test_virtual_VHookCore_nativeRedirectCamera(JNIEnv *env, jclass clazz, jstring video_path) {
    LOGI("Camera redirection requested.");
}
