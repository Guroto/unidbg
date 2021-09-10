package com.shumei.xhs;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.dvm.*;
import com.kero.common.BaseAndroidEmulator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SMSDK3 extends BaseAndroidEmulator {
    public static String risk_json = "{\"all_atamper\":true,\"core_atamper\":true,\"hook_java_switch\":true,\"hook_switch\":false,\"risk_apps\":[{\"xposed\":{\"pn\":\"de.robv.android.xposed.installer\",\"uri\":\"\"}},{\"controllers\":{\"pn\":\"com.soft.controllers\",\"uri\":\"\"}},{\"apk008v\":{\"pn\":\"com.soft.apk008v\",\"uri\":\"\"}},{\"apk008Tool\":{\"pn\":\"com.soft.apk008Tool\",\"uri\":\"\"}},{\"ig\":{\"pn\":\"com.doubee.ig\",\"uri\":\"\"}},{\"anjian\":{\"pn\":\"com.cyjh.mobileanjian\",\"uri\":\"\"}},{\"rktech\":{\"pn\":\"com.ruokuai.rktech\",\"uri\":\"\"}},{\"magisk\":{\"pn\":\"com.topjohnwu.magisk\",\"uri\":\"\"}},{\"kinguser\":{\"pn\":\"com.kingroot.kinguser\",\"uri\":\"\"}},{\"substrate\":{\"pn\":\"com.saurik.substrate\",\"uri\":\"\"}},{\"touchsprite\":{\"pn\":\"com.touchsprite.android\",\"uri\":\"\"}},{\"scriptdroid\":{\"pn\":\"com.stardust.scriptdroid\",\"uri\":\"\"}},{\"toolhero\":{\"pn\":\"com.mobileuncle.toolhero\",\"uri\":\"\"}},{\"huluxia\":{\"pn\":\"com.huluxia.gametools\",\"uri\":\"\"}},{\"apkeditor\":{\"pn\":\"com.gmail.heagoo.apkeditor.pro\",\"uri\":\"\"}},{\"xposeddev\":{\"pn\":\"com.sollyu.xposed.hook.model.dev\",\"uri\":\"\"}},{\"anywhere\":{\"pn\":\"com.txy.anywhere\",\"uri\":\"\"}},{\"burgerzwsm\":{\"pn\":\"pro.burgerz.wsm.manager\",\"uri\":\"\"}},{\"vdloc\":{\"pn\":\"com.virtualdroid.loc\",\"uri\":\"\"}},{\"vdtxl\":{\"pn\":\"com.virtualdroid.txl\",\"uri\":\"\"}},{\"vdwzs\":{\"pn\":\"com.virtualdroid.wzs\",\"uri\":\"\"}},{\"vdkit\":{\"pn\":\"com.virtualdroid.kit\",\"uri\":\"\"}},{\"vdwxg\":{\"pn\":\"com.virtualdroid.wxg\",\"uri\":\"\"}},{\"vdgps\":{\"pn\":\"com.virtualdroid.gps\",\"uri\":\"\"}},{\"a1024mloc\":{\"pn\":\"top.a1024bytes.mockloc.ca.pro\",\"uri\":\"\"}},{\"drhgz\":{\"pn\":\"com.deruhai.guangzi.noroot2\",\"uri\":\"\"}},{\"yggb\":{\"pn\":\"com.mcmonjmb.yggb\",\"uri\":\"\"}},{\"xsrv\":{\"pn\":\"xiake.xserver\",\"uri\":\"\"}},{\"fakeloc\":{\"pn\":\"com.dracrays.fakeloc\",\"uri\":\"\"}},{\"ultra\":{\"pn\":\"net.anylocation.ultra\",\"uri\":\"\"}},{\"locationcheater\":{\"pn\":\"com.wifi99.android.locationcheater\",\"uri\":\"\"}},{\"dwzs\":{\"pn\":\"com.dingweizshou\",\"uri\":\"\"}},{\"mockloc\":{\"pn\":\"top.a1024bytes.mockloc.ca.pro\",\"uri\":\"\"}},{\"anywhereclone\":{\"pn\":\"com.txy.anywhere.clone\",\"uri\":\"\"}},{\"fakelocc\":{\"pn\":\"com.dracrays.fakelocc\",\"uri\":\"\"}},{\"mockwxlocation\":{\"pn\":\"com.tandy.android.mockwxlocation\",\"uri\":\"\"}},{\"anylocation\":{\"pn\":\"net.anylocation\",\"uri\":\"\"}},{\"totalcontrol\":{\"pn\":\"com.sigma_rt.totalcontrol\",\"uri\":\"\"}},{\"ipjl2\":{\"pn\":\"com.chuangdian.ipjl2\",\"uri\":\"\"}}],\"risk_dirs\":[{\"008Mode\":{\"dir\":\".system/008Mode\",\"type\":\"sdcard\"}},{\"008OK\":{\"dir\":\".system/008OK\",\"type\":\"sdcard\"}},{\"008system\":{\"dir\":\".system/008system\",\"type\":\"sdcard\"}},{\"iGrimace\":{\"dir\":\"iGrimace\",\"type\":\"sdcard\"}},{\"touchelper\":{\"dir\":\"/data/data/net.aisence.Touchelper\",\"type\":\"absolute\"}},{\"elfscript\":{\"dir\":\"/mnt/sdcard/touchelf/scripts/\",\"type\":\"absolute\"}},{\"spritelua\":{\"dir\":\"/mnt/sdcard/TouchSprite/lua\",\"type\":\"absolute\"}},{\"spritelog\":{\"dir\":\"/mnt/sdcard/TouchSprite/log\",\"type\":\"absolute\"}},{\"assistant\":{\"dir\":\"/data/data/com.xxAssistant\",\"type\":\"absolute\"}},{\"assistantscript\":{\"dir\":\"/mnt/sdcard/com.xxAssistant/script\",\"type\":\"absolute\"}},{\"mobileanjian\":{\"dir\":\"/data/data/com.cyjh.mobileanjian\",\"type\":\"absolute\"}}],\"risk_file_switch\":true,\"risk_files\":\"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\",\"sensitive.ainfo\":true,\"sensitive.apps\":true,\"sensitive.aps\":true,\"sensitive.bssid\":true,\"sensitive.camera\":true,\"sensitive.cell\":true,\"sensitive.gps\":false,\"sensitive.iccid\":true,\"sensitive.imsi\":true,\"sensitive.mac\":true,\"sensitive.ssid\":true,\"sensitive.tel\":false,\"sysappcnt\":0,\"usrappcnt\":0,\"white_apps\":[]}";
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
        smsdk.y1(risk_json);
    }

    public String y1(String data) {
        List<Object> params = new ArrayList<>(6);
        params.add(vm.getJNIEnv());
        params.add(0);
        DvmObject<?> obj = vm.resolveClass("android/content/Context").newObject(null);
        params.add(vm.addLocalObject(obj));
        params.add(1);
        params.add(vm.addLocalObject(new StringObject(vm, risk_json)));
        params.add(1);
        params.add(vm.addLocalObject(null));

        Number number = module.callFunction(emulator, 0x1A734, params.toArray())[0];
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println(result);
        return result;
    }
}
