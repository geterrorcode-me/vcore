package com.aar.test.virtual;

// IMPORT INI YANG KURANG:
import android.content.Context; 
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class VPMSProxy implements InvocationHandler {
    private static final String TAG = "VPhoneOS-PMS";
    private final Object mBase;
    private final String mTargetPkg;
    private final String mApkPath;

    public VPMSProxy(Object base, String targetPkg, String apkPath) {
        this.mBase = base;
        this.mTargetPkg = targetPkg;
        this.mApkPath = apkPath;
    }

    public static void inject(Context context, String targetPkg, String apkPath) {
        try {
            // 1. Ambil ActivityThread
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            // 2. Ambil sPackageManager asli (Gunakan Field agar lebih pasti)
            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object basePackageManager = sPackageManagerField.get(null);

            // 3. Buat Proxy untuk IPackageManager
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(
                    iPackageManagerInterface.getClassLoader(),
                    new Class<?>[]{iPackageManagerInterface},
                    new VPMSProxy(basePackageManager, targetPkg, apkPath)
            );

            // 4. Suntikkan balik ke sPackageManager (Global Hook)
            sPackageManagerField.set(null, proxy);

            // 5. Hook juga mPM di dalam ContextImpl milik aplikasi agar lokalnya juga berubah
            try {
                android.content.pm.PackageManager pm = context.getPackageManager();
                Field mPmField = pm.getClass().getDeclaredField("mPM");
                mPmField.setAccessible(true);
                mPmField.set(pm, proxy);
            } catch (Exception ignored) {}

            Log.i(TAG, "PMS Hooked successfully for " + targetPkg);
        } catch (Exception e) {
            Log.e(TAG, "Gagal Hook PMS: " + e.getMessage());
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        
        // Intercept pengecekan PackageInfo
        if ("getPackageInfo".equals(methodName)) {
            String pkgName = (String) args[0];
            if (mTargetPkg.equals(pkgName)) {
                return createFakePackageInfo();
            }
        }
        
        // Intercept pengecekan ApplicationInfo
        if ("getApplicationInfo".equals(methodName)) {
            String pkgName = (String) args[0];
            if (mTargetPkg.equals(pkgName)) {
                return createFakeApplicationInfo();
            }
        }

        // Jalankan method asli untuk package lain
        return method.invoke(mBase, args);
    }

    private PackageInfo createFakePackageInfo() {
        PackageInfo info = new PackageInfo();
        info.packageName = mTargetPkg;
        info.applicationInfo = createFakeApplicationInfo();
        // Tambahkan flag agar dianggap terinstall
        return info;
    }

    private ApplicationInfo createFakeApplicationInfo() {
        ApplicationInfo info = new ApplicationInfo();
        info.packageName = mTargetPkg;
        info.sourceDir = mApkPath;
        info.publicSourceDir = mApkPath;
        // Gunakan path internal agar tidak kena permission eksternal
        info.dataDir = "/data/data/" + mTargetPkg; 
        return info;
    }
}
