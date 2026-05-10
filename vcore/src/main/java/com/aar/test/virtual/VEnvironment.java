package com.aar.test.virtual;

import android.util.Log;
import java.lang.reflect.Method;

public class VEnvironment {
    private static final String TAG = "VPhoneOS-Env";

    public static void init() {
        try {
            Class<?> vmRuntimeClass = Class.forName("dalvik.system.VMRuntime");
            Method getRuntimeMethod = vmRuntimeClass.getDeclaredMethod("getRuntime");
            getRuntimeMethod.setAccessible(true);
            Object runtime = getRuntimeMethod.invoke(null);

            Method setExemptionsMethod = vmRuntimeClass.getDeclaredMethod("setHiddenApiExemptions", String[].class);
            setExemptionsMethod.setAccessible(true);
            
            // Bypass all hidden API restrictions
            setExemptionsMethod.invoke(runtime, new Object[]{new String[]{"L"}});
            
            Log.i(TAG, "Hidden API Bypass: SUCCESS");
        } catch (Exception e) {
            Log.e(TAG, "Hidden API Bypass: FAILED -> " + e.getMessage());
        }
    }
}
