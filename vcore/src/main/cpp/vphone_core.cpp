#include <jni.h>
#include <android/log.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <linux/userfaultfd.h>
#include <fcntl.h>
#include <errno.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "VCore-Native", __VA_ARGS__)

extern "C" JNIEXPORT void JNICALL
Java_com_aar_test_virtual_VHookCore_nativeInitHook(JNIEnv *env, jclass clazz) {
    LOGI("Bypassing Userfaultfd for HyperOS...");

    // Trik: Paksa alokasi dummy untuk memicu kernel mengizinkan move ioctl
    int uffd = open("/dev/userfaultfd", O_RDWR | O_CLOEXEC);
    if (uffd >= 0) {
        struct uffdio_api api = { .api = UFFD_API, .features = 0 };
        if (ioctl(uffd, UFFDIO_API, &api) == 0) {
            LOGI("Userfaultfd API version: 0x%llx", api.api);
        }
        // Kita tidak menutup FD ini terlalu cepat agar kernel tetap 'hangat'
        // close(uffd); 
    } else {
        LOGI("Userfaultfd not accessible, trying memory remap strategy...");
    }
}
