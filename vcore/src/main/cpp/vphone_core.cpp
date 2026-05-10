#include <jni.h>
#include <android/log.h>
#include <sys/system_properties.h>
#include <unistd.h>
#include <sys/syscall.h>
#include <fcntl.h>      // WAJIB: Untuk O_CLOEXEC dan O_NONBLOCK
#include <sys/ioctl.h>
#include <errno.h>

// Memastikan __NR_userfaultfd tersedia jika tidak ditemukan di header standar
#ifndef __NR_userfaultfd
  #if defined(__aarch64__)
    #define __NR_userfaultfd 323
  #elif defined(__arm__)
    #define __NR_userfaultfd 374
  #endif
#endif

#define LOG_TAG "VCore-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT void JNICALL
Java_com_aar_test_virtual_VHookCore_nativeInitHook(JNIEnv *env, jclass clazz) {
    LOGI(">>> EXECUTING NATIVE NUCLEAR BYPASS <<<");

    // 1. Bypass Hidden API via Native Property
    // Ini membantu melonggarkan verifikasi runtime pada HyperOS
    __system_property_set("persist.device_config.runtime_native.use_app_image_startup_cache", "false");

    // 2. Fix Userfaultfd via Syscall
    // Menggunakan flag O_CLOEXEC (02000000) dan O_NONBLOCK (00004000)
    long uffd = syscall(__NR_userfaultfd, O_CLOEXEC | O_NONBLOCK);
    
    if (uffd < 0) {
        LOGI("UFFD Syscall Failed: %s (errno: %d)", strerror(errno), errno);
    } else {
        LOGI("UFFD Syscall SUCCESS. File Descriptor: %ld", uffd);
        // Jangan langsung ditutup agar kernel tetap mempertahankan konteksnya
    }

    LOGI("Native Nuclear Bypass Finished.");
}

extern "C" JNIEXPORT void JNICALL
Java_com_aar_test_virtual_VHookCore_nativeRedirectCamera(JNIEnv *env, jclass clazz, jstring video_path) {
    // Placeholder untuk logika Hook Camera selanjutnya
    LOGI("Camera redirection requested.");
}
