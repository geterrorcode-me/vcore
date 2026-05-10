package com.aar.test.virtual;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.Method;

/**
 * VHookCore adalah jantung dari Virtual Engine.
 * Bertanggung jawab atas inisialisasi bypass sistem, pemuatan native library,
 * dan penyuntikan proxy identitas aplikasi.
 */
public class VHookCore {
    private static final String TAG = "VPhoneOS-HookCore";
    private static boolean isInstalled = false;

    static {
        try {
            // Memuat library native C++ untuk menangani memory & syscall hook
            System.loadLibrary("vphone_core");
            Log.i(TAG, "Native Library 'libvphone_core.so' loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "CRITICAL: Native Library not found! Check your AAR/JNI build.");
        }
    }

    /**
     * Memasang semua hook yang diperlukan untuk menjalankan aplikasi virtual.
     */
    public static void install(Context context, String targetPkg, String apkPath) {
        if (isInstalled) {
            Log.w(TAG, "HookCore already installed. Skipping...");
            return;
        }

        Log.i(TAG, ">>> Installing Virtual Engine for: " + targetPkg + " <<<");

        // 1. Bypass Hidden API (Membuka gembok internal Android 11-14)
        bypassHiddenApi();

        // 2. PMS Hooking (Memalsukan identitas paket/APK)
        try {
            VPMSProxy.inject(context, targetPkg, apkPath);
            Log.i(TAG, "Identity Proxy (PMS) injected.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to inject PMS Proxy: " + e.getMessage());
        }

        // 3. Native Initialization (Fix userfaultfd & Layar Putih)
        try {
            nativeInitHook();
            Log.i(TAG, "Native Memory Bridge initialized.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native method 'nativeInitHook' not found!");
        }

        isInstalled = true;
    }

    /**
     * Teknik Meta-Reflection untuk mematikan proteksi Hidden API di Android 14.
     */
    private static void bypassHiddenApi() {
        try {
            // Kita memanggil refleksi di dalam refleksi agar tidak terdeteksi sistem
            Method forNameMethod = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);

            Class<?> vmRuntimeClass = (Class<?>) forNameMethod.invoke(null, "dalvik.system.VMRuntime");
            Method getRuntimeMethod = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", (Object) null);
            Object runtime = getRuntimeMethod.invoke(null);

            Method setExemptionsMethod = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            
            // "L" adalah wildcard untuk mengizinkan SEMUA API internal
            setExemptionsMethod.invoke(runtime, new Object[]{new String[]{"L"}});
            Log.i(TAG, "Hidden API Bypass: SUCCESS (Meta-Reflection)");
        } catch (Exception e) {
            Log.e(TAG, "Hidden API Bypass: FAILED -> " + e.getMessage());
        }
    }

    /**
     * Mengarahkan input kamera ke file video tertentu.
     * @param videoPath Path lengkap ke file .mp4
     */
    public static void setVirtualCamera(String videoPath) {
        try {
            nativeRedirectCamera(videoPath);
            Log.i(TAG, "Camera redirection set to: " + videoPath);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native camera redirect not available.");
        }
    }

    // --- Native Methods ---
    
    /**
     * Inisialisasi hook tingkat rendah (C++) untuk memori dan syscall.
     */
    private static native void nativeInitHook();

    /**
     * Melakukan inline hooking pada driver kamera di level native.
     */
    private static native void nativeRedirectCamera(String videoPath);
}
