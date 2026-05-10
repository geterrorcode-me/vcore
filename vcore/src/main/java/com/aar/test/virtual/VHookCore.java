package com.aar.test.virtual;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.Method;

public class VHookCore {
    private static final String TAG = "VPhoneOS-HookCore";
    private static boolean isInstalled = false;

    static {
        try {
            System.loadLibrary("vphone_core");
            Log.i(TAG, "Native Library 'libvphone_core.so' loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native Library NOT FOUND! Check your AAR build.");
        }
    }

    /**
     * Inti dari inisialisasi lingkungan virtual.
     */
    public static void install(Context context, String targetPkg, String apkPath) {
        if (isInstalled) return;

        Log.i(TAG, ">>> Installing Virtual Engine for: " + targetPkg + " <<<");

        // 1. Bypass Hidden API (Fix FAILED status on Android 14)
        bypassHiddenApi();

        // 2. PMS Hooking (Memalsukan identitas APK)
        try {
            VPMSProxy.inject(context, targetPkg, apkPath);
        } catch (Exception e) {
            Log.e(TAG, "PMS Injection failed: " + e.getMessage());
        }

        // 3. Native Hook (Fix Userfaultfd & Camera logic)
        try {
            nativeInitHook();
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native method init failed.");
        }

        isInstalled = true;
    }

    /**
     * Teknik Meta-Reflection untuk menembus proteksi API internal Android 14.
     */
    private static void bypassHiddenApi() {
        try {
            // Gunakan refleksi ganda untuk membingungkan verifikasi sistem
            Method forName = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);

            Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
            Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", (Object) null);
            Object runtime = getRuntime.invoke(null);

            Method setExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            
            // Memberikan pengecualian untuk semua library (L)
            setExemptions.invoke(runtime, new Object[]{new String[]{"L"}});
            Log.i(TAG, "Hidden API Bypass: GRANTED (Android 14 Optimized)");
        } catch (Exception e) {
            Log.e(TAG, "Hidden API Bypass: CRITICAL FAILURE -> " + e.toString());
        }
    }

    public static void setVirtualCamera(String videoPath) {
        try {
            nativeRedirectCamera(videoPath);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native camera redirect failed.");
        }
    }

    private static native void nativeInitHook();
    private static native void nativeRedirectCamera(String videoPath);
}
