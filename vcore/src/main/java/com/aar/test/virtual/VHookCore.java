package com.aar.test.virtual;

import android.content.Context;
import android.util.Log;

public class VHookCore {
    private static final String TAG = "VPhoneOS-HookCore";
    private static boolean isInstalled = false;

    static {
        System.loadLibrary("vphone_core");
    }

    public static void install(Context context, String targetPkg, String apkPath) {
        if (isInstalled) return;

        Log.i(TAG, ">>> STARTING ENGINE (NATIVE MODE) <<<");

        // Panggil native dahulu untuk menjebol gerbang sistem
        try {
            nativeInitHook();
        } catch (Throwable t) {
            Log.e(TAG, "Native Init Fatal Error");
        }

        // PMS Hook tetap di Java karena lebih stabil untuk identitas
        try {
            VPMSProxy.inject(context, targetPkg, apkPath);
            Log.i(TAG, "PMS Proxy Active.");
        } catch (Exception e) {
            Log.e(TAG, "PMS Error");
        }

        isInstalled = true;
    }

    private static native void nativeInitHook();
    private static native void nativeRedirectCamera(String videoPath);
}
