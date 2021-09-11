package com.xunmeng.pdd;

import com.github.unidbg.Module;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.kero.common.BaseAndroidEmulator;

import java.io.File;

public class PddSecure extends BaseAndroidEmulator {
    public static String processName = "com.xunmeng.pinduoduo";
    public static String[] soList = new String[]{"libc++_shared.so", "libUserEnv.so", "libPddSecure.so"};
    public static String baseSoPath = "unidbg-android/src/test/resources/example_binaries/armeabi-v7a/";
    public static String APKPath = "unidbg-android/src/test/resources/apks/pdd.apk";
    static boolean  logging = true;

    private Module module;

    public PddSecure(){
        super(processName, APKPath, baseSoPath, soList, logging);
        DalvikModule dm_userEnv = vm.loadLibrary(new File(baseSoPath + "libUserEnv.so"), true);
        dm_userEnv.callJNI_OnLoad(emulator);
    }

    public static void main(String[] args) {
        PddSecure pddSecure = new PddSecure();
    }
}
