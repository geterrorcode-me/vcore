package com.aar.test.virtual;

import android.content.Context;
import android.util.Log;

public class VHookCore {
    private static final String TAG = "VPhoneOS-HookCore";
    private static boolean isInstalled = false;

    static {
        try {
            System.loadLibrary("vphone_core");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Gagal memuat native library: " + e.getMessage());
        }
    }

    public static void install(Context context, String targetPkg, String apkPath) {
        if (isInstalled) return;
        
        Log.i(TAG, "Installing Virtual Engine for: " + targetPkg);

        // 1. Jalankan Bypass Saraf Pusat
        VEnvironment.init();

        // 2. Suntikkan PMS Proxy (Identitas)
        VPMSProxy.inject(context, targetPkg, apkPath);

        // 3. Jalankan Native Hook (Fix Userfaultfd/Memory)
        try {
            nativeInitHook();
        } catch (Exception e) {
            Log.e(TAG, "Native Hook Error: " + e.getMessage());
        }

        isInstalled = true;
    }

    public static void setVirtualCamera(String videoFilePath) {
        nativeRedirectCamera(videoFilePath);
    }

    private static native void nativeInitHook();
    private static native void nativeRedirectCamera(String videoPath);
}
