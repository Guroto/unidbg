package com.com.kero.learning;

import com.github.unidbg.Module;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.StringObject;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.kero.common.BaseAndroidEmulator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XC extends BaseAndroidEmulator {
    public static String processName = "ctrip.android.view";
    public static String[] soList = new String[]{"libscmain.so"};
    public static String baseSoPath = "unidbg-android/src/test/resources/example_binaries/armeabi-v7a/";
    public static String APKPath = "unidbg-android/src/test/resources/apks/xc 8-38-2.apk";
    static boolean  logging = true;

    public XC(){
        super(processName, APKPath, baseSoPath, soList, logging);
    }

    public static void main(String[] args) {
        XC xc = new XC();
        xc.SimpleSign();
    }

    public void SimpleSign(){
        List<Object> params = new ArrayList<>(10);
        params.add(vm.getJNIEnv());
        params.add(0);
        String input = "7be9f13e7f5426d139cb4e5dbb1fdba7";
        byte[] inputByte = input.getBytes();
        ByteArray inputByteArray = new ByteArray(vm,inputByte);
        params.add(vm.addLocalObject(inputByteArray));
        params.add(vm.addLocalObject(new StringObject(vm, "getdata")));
        Number number = module.callFunction(emulator, 0x869d9, params.toArray())[0];
        System.out.println(vm.getObject(number.intValue()).getValue().toString());
    }
}
