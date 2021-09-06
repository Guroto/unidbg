package com.shumei.xhs;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.StringObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.android.dvm.jni.ProxyClassFactory;
import com.github.unidbg.memory.Memory;
import com.kero.crypto.AES;

import java.io.File;
import java.io.IOException;

public class SMSDK {
    static String risk_json = "{\"all_atamper\":true,\"core_atamper\":true,\"hook_java_switch\":true,\"hook_switch\":false,\"risk_apps\":[{\"xposed\":{\"pn\":\"de.robv.android.xposed.installer\",\"uri\":\"\"}},{\"controllers\":{\"pn\":\"com.soft.controllers\",\"uri\":\"\"}},{\"apk008v\":{\"pn\":\"com.soft.apk008v\",\"uri\":\"\"}},{\"apk008Tool\":{\"pn\":\"com.soft.apk008Tool\",\"uri\":\"\"}},{\"ig\":{\"pn\":\"com.doubee.ig\",\"uri\":\"\"}},{\"anjian\":{\"pn\":\"com.cyjh.mobileanjian\",\"uri\":\"\"}},{\"rktech\":{\"pn\":\"com.ruokuai.rktech\",\"uri\":\"\"}},{\"magisk\":{\"pn\":\"com.topjohnwu.magisk\",\"uri\":\"\"}},{\"kinguser\":{\"pn\":\"com.kingroot.kinguser\",\"uri\":\"\"}},{\"substrate\":{\"pn\":\"com.saurik.substrate\",\"uri\":\"\"}},{\"touchsprite\":{\"pn\":\"com.touchsprite.android\",\"uri\":\"\"}},{\"scriptdroid\":{\"pn\":\"com.stardust.scriptdroid\",\"uri\":\"\"}},{\"toolhero\":{\"pn\":\"com.mobileuncle.toolhero\",\"uri\":\"\"}},{\"huluxia\":{\"pn\":\"com.huluxia.gametools\",\"uri\":\"\"}},{\"apkeditor\":{\"pn\":\"com.gmail.heagoo.apkeditor.pro\",\"uri\":\"\"}},{\"xposeddev\":{\"pn\":\"com.sollyu.xposed.hook.model.dev\",\"uri\":\"\"}},{\"anywhere\":{\"pn\":\"com.txy.anywhere\",\"uri\":\"\"}},{\"burgerzwsm\":{\"pn\":\"pro.burgerz.wsm.manager\",\"uri\":\"\"}},{\"vdloc\":{\"pn\":\"com.virtualdroid.loc\",\"uri\":\"\"}},{\"vdtxl\":{\"pn\":\"com.virtualdroid.txl\",\"uri\":\"\"}},{\"vdwzs\":{\"pn\":\"com.virtualdroid.wzs\",\"uri\":\"\"}},{\"vdkit\":{\"pn\":\"com.virtualdroid.kit\",\"uri\":\"\"}},{\"vdwxg\":{\"pn\":\"com.virtualdroid.wxg\",\"uri\":\"\"}},{\"vdgps\":{\"pn\":\"com.virtualdroid.gps\",\"uri\":\"\"}},{\"a1024mloc\":{\"pn\":\"top.a1024bytes.mockloc.ca.pro\",\"uri\":\"\"}},{\"drhgz\":{\"pn\":\"com.deruhai.guangzi.noroot2\",\"uri\":\"\"}},{\"yggb\":{\"pn\":\"com.mcmonjmb.yggb\",\"uri\":\"\"}},{\"xsrv\":{\"pn\":\"xiake.xserver\",\"uri\":\"\"}},{\"fakeloc\":{\"pn\":\"com.dracrays.fakeloc\",\"uri\":\"\"}},{\"ultra\":{\"pn\":\"net.anylocation.ultra\",\"uri\":\"\"}},{\"locationcheater\":{\"pn\":\"com.wifi99.android.locationcheater\",\"uri\":\"\"}},{\"dwzs\":{\"pn\":\"com.dingweizshou\",\"uri\":\"\"}},{\"mockloc\":{\"pn\":\"top.a1024bytes.mockloc.ca.pro\",\"uri\":\"\"}},{\"anywhereclone\":{\"pn\":\"com.txy.anywhere.clone\",\"uri\":\"\"}},{\"fakelocc\":{\"pn\":\"com.dracrays.fakelocc\",\"uri\":\"\"}},{\"mockwxlocation\":{\"pn\":\"com.tandy.android.mockwxlocation\",\"uri\":\"\"}},{\"anylocation\":{\"pn\":\"net.anylocation\",\"uri\":\"\"}},{\"totalcontrol\":{\"pn\":\"com.sigma_rt.totalcontrol\",\"uri\":\"\"}},{\"ipjl2\":{\"pn\":\"com.chuangdian.ipjl2\",\"uri\":\"\"}}],\"risk_dirs\":[{\"008Mode\":{\"dir\":\".system/008Mode\",\"type\":\"sdcard\"}},{\"008OK\":{\"dir\":\".system/008OK\",\"type\":\"sdcard\"}},{\"008system\":{\"dir\":\".system/008system\",\"type\":\"sdcard\"}},{\"iGrimace\":{\"dir\":\"iGrimace\",\"type\":\"sdcard\"}},{\"touchelper\":{\"dir\":\"/data/data/net.aisence.Touchelper\",\"type\":\"absolute\"}},{\"elfscript\":{\"dir\":\"/mnt/sdcard/touchelf/scripts/\",\"type\":\"absolute\"}},{\"spritelua\":{\"dir\":\"/mnt/sdcard/TouchSprite/lua\",\"type\":\"absolute\"}},{\"spritelog\":{\"dir\":\"/mnt/sdcard/TouchSprite/log\",\"type\":\"absolute\"}},{\"assistant\":{\"dir\":\"/data/data/com.xxAssistant\",\"type\":\"absolute\"}},{\"assistantscript\":{\"dir\":\"/mnt/sdcard/com.xxAssistant/script\",\"type\":\"absolute\"}},{\"mobileanjian\":{\"dir\":\"/data/data/com.cyjh.mobileanjian\",\"type\":\"absolute\"}}],\"risk_file_switch\":true,\"risk_files\":\"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\",\"sensitive.ainfo\":true,\"sensitive.apps\":true,\"sensitive.aps\":true,\"sensitive.bssid\":true,\"sensitive.camera\":true,\"sensitive.cell\":true,\"sensitive.gps\":false,\"sensitive.iccid\":true,\"sensitive.imsi\":true,\"sensitive.mac\":true,\"sensitive.ssid\":true,\"sensitive.tel\":false,\"sysappcnt\":0,\"usrappcnt\":0,\"white_apps\":[]}";
    static String public_key = "MIIDOzCCAiOgAwIBAgIBMDANBgkqhkiG9w0BAQUFADA4MQswCQYDVQQGEwJDTjENMAsGA1UECwwEQ05DQjEaMBgGA1UEAwwRZS5iYW5rLmVjaXRpYy5jb20wHhcNMTgwMjExMDg0NTIyWhcNMzgwMjA2MDg0NTIyWjA4MQswCQYDVQQGEwJDTjENMAsGA1UECwwEQ05DQjEaMBgGA1UEAwwRZS5iYW5rLmVjaXRpYy5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCkF+2AicVKj7SaHw3dbJt3i6fkL1WfLw1WRqe8r8Cc7qJOshaqNvCzW1qRX6E5H/umtl1Uj99V07uewUFk96xY/+s/GuBnbGoSrcu3OAHDgEGuY5atZo+umIk7LufAif2VUcNGY3nWxGcig20ExO/6nAf/G3Xxo4QL8fBdPG/prOXxSvtJiPls1Qg9zzSgAH+HMCAINMsuJmzDQiTt6Me8k7YHts+jWQF7KF25plITcW1Qmy3Aw8qYjVhbHn8KTAEeuQhmM5RS6KP1Hu71q4DYOWcx44QThSbiAYwG1JQBBwM8XnBfVYMpr6Qi0owibNYoZ/S6xwfRFGB0W1HeG9WfAgMBAAGjUDBOMB0GA1UdDgQWBBT0iLEXY9HIKNy5DG4d72l+R7Nf1zAfBgNVHSMEGDAWgBT0iLEXY9HIKNy5DG4d72l+R7Nf1zAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQB5MWz1RGFG537rJCtHp+LqxR9iJSFsHiW3ZoLIAeyD0oJ69RcL2gE/TNWmE9zYUkd9TdNtXqxlNPpj1P1/+x781neWnGou/n/XFS82T5S339X3DIjHc/IqOzwnxEOKH2V0NmK9iKgx6H05Q9MMvUXFsL3QK2hDMAVY28roRiC4S1yfJJaA08DfvXZf6cVx1xfWl+ks57+3knkoWap1rjwh1RdGk5ChPbzD0AnAcWTMWRCbjuJnttlmWZnI1I6mhcQUKUEMoj8sR8m11YJ5woscYPsIle/rJOOosuMghczD1vRcg3eLUaWn1A5rsBa82RyxhiuYocEQVX59Hy6v3npT";

    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    private final DvmClass SMSDK;
    private final String soName = "libsmsdk.so";
    private final String soPath = "unidbg-android/src/test/resources/example_binaries/armeabi-v7a" + soName;
    private final boolean logging;

    private static AndroidEmulator createARMEmulator() {
        return AndroidEmulatorBuilder.for32Bit()
                .setProcessName("com.xingin.xhs")
                .addBackendFactory(new DynarmicFactory(true))
                .build();
    }

    public SMSDK(boolean logging){
        this.logging = logging;
        emulator =  createARMEmulator();
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));

        vm = emulator.createDalvikVM();
        vm.setDvmClassFactory(new ProxyClassFactory());
        vm.setVerbose(logging);

        DalvikModule dm = vm.loadLibrary(new File(soPath), false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();

        SMSDK = vm.resolveClass("com/ishumei/dfp/SMSDK");
    }

    void destroy() throws IOException {
        emulator.close();
        if (logging) {
            System.out.println("destroy");
        }
    }

    public StringObject x2(String data, String randomString) {
        return SMSDK.callStaticJniMethodObject(emulator, "x2(Ljava/lang/String;Ljava/lang/String;)", randomString, data);
    }

    public static String y1(String y1Data, String key) {
        byte[] iv = new byte[]{(byte)0x30, (byte)0x31, (byte)0x30, (byte)0x32, (byte)0x30, (byte)0x33, (byte)0x30, (byte)0x34, (byte)0x30, (byte)0x35, (byte)0x30, (byte)0x36, (byte)0x30, (byte)0x37, (byte)0x30, (byte)0x38};
        return AES.encrypt(y1Data.getBytes(), key.getBytes(), iv);
    }

    public static byte[] encode(String str) {
        byte[] bytes = str.getBytes();
        int length = bytes.length;
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) Integer.parseInt(new String(bytes, i, 2), 16);
        }
        return bArr;
    }

    public static byte[] encode2(byte[] arg3) {
        byte[] v0 = new byte[arg3.length];
        int v1;
        for (v1 = 0; v1 < arg3.length; ++v1) {
            v0[v1] = (byte) (~arg3[v1]);
        }
        return v0;
    }
}
