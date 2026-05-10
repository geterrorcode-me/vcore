package com.aar.test.virtual;

import android.util.Log;
import java.lang.reflect.Method;

public class VEnvironment {
    public static void init() {
        try {
            // Meta-reflection: Memanggil forName lewat class Class itu sendiri
            Method forNameMethod = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);

            Class<?> vmRuntimeClass = (Class<?>) forNameMethod.invoke(null, "dalvik.system.VMRuntime");
            Method getRuntimeMethod = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", (Object) null);
            Object runtime = getRuntimeMethod.invoke(null);

            Method setExemptionsMethod = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            
            // "L" membebaskan SEMUA hidden API
            setExemptionsMethod.invoke(runtime, new Object[]{new String[]{"L"}});
            Log.i("VPhoneOS-Env", "Bypass Hidden API Berhasil via Meta-Reflection!");
        } catch (Exception e) {
            Log.e("VPhoneOS-Env", "Bypass Gagal Total: " + e.getMessage());
        }
    }
}
