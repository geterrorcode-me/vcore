package com.aar.test.virtual;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.Method;

/**
 * VHookCore: Engine Utama Virtual OS
 * Dioptimalkan khusus untuk menembus proteksi Android 14 HyperOS (Xiaomi)
 */
public class VHookCore {
    private static final String TAG = "VPhoneOS-HookCore";
    private static boolean isInstalled = false;

    static {
        try {
            // Memuat library native untuk penanganan memori & syscall
            System.loadLibrary("vphone_core");
            Log.i(TAG, "Native Library 'libvphone_core.so' loaded.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "CRITICAL: Native Library not found!");
        }
    }

    /**
     * Memasang hook sistem untuk aplikasi target
     */
    public static void install(Context context, String targetPkg, String apkPath) {
        if (isInstalled) return;

        Log.i(TAG, ">>> INITIATING HARD-HOOK FOR: " + targetPkg + " <<<");

        // 1. BYPASS HIDDEN API (Solusi Fix InvocationTargetException)
        applyHyperOSBypass();

        // 2. PMS PROXY (Memalsukan Identitas APK)
        try {
            VPMSProxy.inject(context, targetPkg, apkPath);
            Log.i(TAG, "Identity Proxy (PMS) Injected.");
        } catch (Exception e) {
            Log.e(TAG, "PMS Injection Failed: " + e.getMessage());
        }

        // 3. NATIVE HANDSHAKE (Fix Userfaultfd Timeout)
        try {
            nativeInitHook();
            Log.i(TAG, "Native System Bridge Ready.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native bridge failed to connect.");
        }

        isInstalled = true;
    }

    /**
     * Teknik Double-Reflection: 
     * Memanggil 'setHiddenApiExemptions' dengan menyamar sebagai sistem inti.
     */
    private static void applyHyperOSBypass() {
        try {
            // Kita tidak memanggil VMRuntime secara langsung, tapi lewat 'Class' handle
            Method getMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            Class<?> vmRuntimeClass = Class.forName("dalvik.system.VMRuntime");
            
            // Dapatkan instance Runtime
            Method getRuntime = (Method) getMethod.invoke(vmRuntimeClass, "getRuntime", (Object) null);
            Object runtime = getRuntime.invoke(null);

            // Dapatkan method pengecualian API
            Method setExemptions = (Method) getMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            
            // "L" adalah wildcard untuk membebaskan SEMUA akses API internal (Hidden API)
            setExemptions.invoke(runtime, new Object[]{new String[]{"L"}});
            
            Log.i(TAG, ">>> HYPER-OS BYPASS GRANTED <<<");
        } catch (Exception e) {
            Log.e(TAG, "Deep Bypass Failed, Fallback to Native logic: " + e.toString());
        }
    }

    /**
     * Mengarahkan output kamera ke file video
     * @param videoPath path ke file mp4 (misal: /sdcard/video.mp4)
     */
    public static void setVirtualCamera(String videoPath) {
        try {
            nativeRedirectCamera(videoPath);
            Log.i(TAG, "Virtual Camera directed to: " + videoPath);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native camera redirect not supported.");
        }
    }

    // --- Native Linkage ---
    private static native void nativeInitHook();
    private static native void nativeRedirectCamera(String videoPath);
}
