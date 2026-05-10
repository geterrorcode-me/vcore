#include <jni.h>
#include <android/log.h>
#include <sys/mman.h>
#include <linux/userfaultfd.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <unistd.h>

#define LOG_TAG "VCore-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT void JNICALL
Java_com_aar_test_virtual_VHookCore_nativeInitHook(JNIEnv *env, jclass clazz) {
    LOGI("Initiating Native Shield Bypass...");

    // 1. Force Open Userfaultfd agar kernel tidak timeout
    int fd = open("/dev/userfaultfd", O_RDWR | O_CLOEXEC);
    if (fd < 0) {
        LOGI("UFFD direct open failed, trying syscall...");
        // Syscall 323 adalah userfaultfd di arm64
        fd = syscall(323, O_CLOEXEC | O_NONBLOCK);
    }

    if (fd >= 0) {
        struct uffdio_api uffdio_api = { .api = UFFD_API, .features = 0 };
        if (ioctl(fd, UFFDIO_API, &uffdio_api) == 0) {
            LOGI("Userfaultfd Handshake SUCCESS");
        }
    }

    LOGI("Native Engine Ready.");
}
