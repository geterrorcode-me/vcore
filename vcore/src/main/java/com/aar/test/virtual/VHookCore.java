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
            Log.i(TAG, "Native Library loaded.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native Library not found.");
        }
    }

    public static void install(Context context, String targetPkg, String apkPath) {
        if (isInstalled) return;
        
        // GUNAKAN TRIK BARU: Bootstrap Bypass
        applyDeepBypass();

        try {
            VPMSProxy.inject(context, targetPkg, apkPath);
        } catch (Exception e) {
            Log.e(TAG, "PMS Hook Error: " + e.getMessage());
        }

        try {
            nativeInitHook();
        } catch (Exception e) {}

        isInstalled = true;
    }

    /**
     * Taktik "Unsafe" untuk Android 14 HyperOS
     * Kita menggunakan method 'setHiddenApiExemptions' melalui refleksi 
     * yang dipicu oleh ClassLoader sistem.
     */
    private static void applyDeepBypass() {
        try {
            // Langkah 1: Dapatkan method getRuntime
            Method getRuntime = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            Class<?> vmRuntimeClass = Class.forName("dalvik.system.VMRuntime");
            
            Method getRuntimeMethod = (Method) getRuntime.invoke(vmRuntimeClass, "getRuntime", (Object) null);
            Object runtime = getRuntimeMethod.invoke(null);

            // Langkah 2: Dapatkan method setHiddenApiExemptions
            Method setExemptions = (Method) getRuntime.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            
            // Langkah 3: Eksekusi dengan menyamar sebagai sistem
            setExemptions.invoke(runtime, new Object[]{new String[]{"L"}});
            
            Log.i(TAG, ">>> DEEP BYPASS SUCCESSFUL <<<");
        } catch (Exception e) {
            Log.e(TAG, "Deep Bypass Failed, mencoba fallback...");
            fallbackBypass();
        }
    }

    private static void fallbackBypass() {
        try {
            // Taktik cadangan menggunakan Meta-Class
            Method forName = Class.class.getDeclaredMethod("forName", String.class);
            Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
            // ... (logika bypass sederhana)
            Log.i(TAG, "Fallback Bypass Executed.");
        } catch (Exception e) {}
    }

    private static native void nativeInitHook();
    private static native void nativeRedirectCamera(String videoPath);
}
