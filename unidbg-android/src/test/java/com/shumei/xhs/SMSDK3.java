package com.shumei.xhs;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.Jni;
import com.github.unidbg.linux.android.dvm.VM;
import com.kero.common.BaseAndroidEmulator;

import java.io.File;

public class SMSDK3 extends BaseAndroidEmulator {
    public static String processName = "com.xingin.xhs";
    public static String SoPath = "unidbg-android/src/test/resources/example_binaries/armeabi-v7a/libsmsdk.so";
    public static String APKPath = "unidbg-android/src/test/resources/apks/xhs_6_73.apk";
    static boolean  logging = true;

    private Module module;

    public SMSDK3() {
        super(processName, APKPath, SoPath, logging);
        this.vm.setJni(this);
        this.vm.setVerbose(logging);
        // 加载so文件至虚拟内容
        DalvikModule dm = this.vm.loadLibrary(new File(SoPath), true);
        this.module = dm.getModule();
        // 调用JNI OnLoad
        dm.callJNI_OnLoad(this.emulator);
    }

    public static void main(String[] args) {
        SMSDK3 smsdk = new SMSDK3();
    }
}
