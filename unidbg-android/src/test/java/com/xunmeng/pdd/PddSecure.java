package com.xunmeng.pdd;

import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.context.Arm32RegisterContext;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.hook.hookzz.HookEntryInfo;
import com.github.unidbg.hook.hookzz.HookZz;
import com.github.unidbg.hook.hookzz.IHookZz;
import com.github.unidbg.hook.hookzz.InstrumentCallback;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.linux.android.SystemPropertyHook;
import com.github.unidbg.memory.MemoryBlock;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.utils.Inspector;
import com.kero.common.BaseAndroidEmulator;
import com.kero.kit.ByteProcess;
import com.kero.kit.gzipProcess;
import com.sun.jna.Pointer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PddSecure extends BaseAndroidEmulator {
    public static String processName = "com.xunmeng.pinduoduo";
    public static String[] soList = new String[]{"libc++_shared.so", "libUserEnv.so"};
    public static String baseSoPath = "unidbg-android/src/test/resources/example_binaries/armeabi-v7a/";
    public static String APKPath = "unidbg-android/src/test/resources/apks/pdd.apk";
    public static DalvikModule dm_userEnv;
    private final Logger logger = Logger.getLogger("LoggingDemo");
    static boolean  verbose = true;

    public PddSecure(){
        super(processName, APKPath, baseSoPath, soList, verbose);
        DalvikModule dm_userEnv = vm.loadLibrary(new File(baseSoPath + "libUserEnv.so"), true);
        dm_userEnv.callJNI_OnLoad(emulator);

        // hook
        PddSecureHook myHook =  new PddSecureHook(emulator, module);
//        myHook.hook(0x0009DA38, 0x0009DA5E, 180);
        myHook.hook(0x47B60, 0x48166, 180);
    }

    public static void main(String[] args) throws IOException {
        Logger.getLogger(String.valueOf(DalvikVM.class)).setLevel(Level.DEBUG);
        Logger.getLogger(String.valueOf(BaseVM.class)).setLevel(Level.DEBUG);
//        Logger.getLogger(String.valueOf(SystemPropertyHook.class)).setLevel(Level.DEBUG);
        PddSecure pddSecure = new PddSecure();
//        pddSecure.deviceInfo2();
//        pddSecure.generateData();
//        pddSecure.inlineHookSub_42B70();
//        pddSecure.keyProcess();
//        pddSecure.inlineHookSub_9DA38();
        pddSecure.UserEnv();
    }

    @Override
    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature){
        switch (signature){
            case "android/provider/Settings$Secure->ANDROID_ID:Ljava/lang/String;":
                return new StringObject(vm, "android_id");
            case "android/content/Intent->ACTION_BATTERY_CHANGED:Ljava/lang/String;":
                return new StringObject(vm, "android.intent.action.BATTERY_CHANGED");
        }
        return super.getStaticObjectField(vm, dvmClass, signature);

    }

    @Override
    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, DvmMethod dvmMethod, VarArg varArg) {
        switch (dvmMethod.getSignature()){
            case "android/provider/Settings$Secure->getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;":{
                String arg = (String) varArg.getObjectArg(1).getValue();
                System.out.println("[Settings$Secure] -> [getString]" + arg);
                return new StringObject(vm, "android_id");
            }
        }
        return super.callStaticObjectMethod(vm, dvmClass, dvmMethod.getSignature(), varArg);
    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "android/provider/Settings$Secure->getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;":
                String arg = (String) vaList.getObjectArg(1).getValue();
                System.out.println("[Settings$Secure] -> [getString]" + arg);
                return new StringObject(vm, "test_android_id");
            case "java/util/UUID->randomUUID()Ljava/util/UUID;":
//                return vm.resolveClass("java/util/UUID").newObject(UUID.randomUUID());
                return vm.resolveClass("java/util/UUID").newObject("512b86b2-fb13-4a35-b67c-ec1520d0ae4d");
        }

        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public void callStaticVoidMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList){
        switch (signature){
            case "com/tencent/mars/xlog/PLog->i(Ljava/lang/String;Ljava/lang/String;)V":
                System.out.println("[ARGS]: " + vaList.getObjectArg(0).getValue());
                System.out.println("[ARGS]: " + vaList.getObjectArg(1).getValue());
                return;
            case "android/view/Display->getRealMetrics(Landroid/util/DisplayMetrics;)V":
                System.out.println("getRealMetricsCALL");
                return;
        }
        super.callStaticVoidMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public int callIntMethod(BaseVM vm, DvmObject<?> dvmObject, DvmMethod dvmMethod, VarArg varArg) {
        switch (dvmMethod.getSignature()){
            case "android/content/Context->checkSelfPermission(Ljava/lang/String;)I":
                return -1;
            case "android/telephony/TelephonyManager->getSimState()I":
                return 1;
            case "android/telephony/TelephonyManager->getNetworkType()I":
                return 13;
            case "android/telephony/TelephonyManager->getDataState()I":
                return 0;
            case "android/telephony/TelephonyManager->getDataActivity()I":
                return 4;
        }
        return super.callIntMethod(vm, dvmObject, dvmMethod.getSignature(), varArg);
    }

    @Override
    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature) {
            case "android/telephony/TelephonyManager->getSimOperatorName()Ljava/lang/String;":
                return new StringObject(vm, "中国联通");
            case "android/telephony/TelephonyManager->getSimCountryIso()Ljava/lang/String;":
                return new StringObject(vm, "cn");
            case "android/telephony/TelephonyManager->getNetworkOperator()Ljava/lang/String;":
                return new StringObject(vm, "46001");
            case "android/telephony/TelephonyManager->getNetworkOperatorName()Ljava/lang/String;":
                return new StringObject(vm, "中国联通");
            case "android/telephony/TelephonyManager->getNetworkCountryIso()Ljava/lang/String;":
                return new StringObject(vm, "cn");
            case "android/content/Context->getContentResolver()Landroid/content/ContentResolver;":
                return vm.resolveClass("android/content/ContentResolver").newObject(signature);
            case "java/lang/Throwable->getStackTrace()[Ljava/lang/StackTraceElement;":
                StackTraceElement[] elements = {
                        new StackTraceElement("com.xunmeng.pinduoduo.secure.DeviceNative", "", "", 0),
                        new StackTraceElement("com.xunmeng.pinduoduo.secure.Settings", "test", "", 0),
                };
                DvmObject[] objs = new DvmObject[elements.length];
                for(int i=0; i<elements.length; i++){
                    objs[i] = vm.resolveClass("java/lang/StackTraceElement").newObject(elements[i]);
                }
                return new ArrayObject(objs);
            case "java/lang/StackTraceElement->getClassName()Ljava/lang/String;":
                StackTraceElement element = (StackTraceElement) dvmObject.getValue();
                return new StringObject(vm, element.getClassName());
            case "android/content/Context->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;":
                return vm.resolveClass("android/content/SharedPreferences").newObject(signature);
            case "android/content/SharedPreferences->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;":
                return new StringObject(vm, "6299d90d187d492ea3c568ef40efe3db");
        }
        return super.callObjectMethod(vm ,dvmObject, signature, varArg);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList){
        switch (signature) {
            case "android/app/ActivityThread->getApplication()Landroid/app/Application;":
                DvmClass context = vm.resolveClass("android/content/Context");
                DvmClass Application = vm.resolveClass("android/app/Application", context);
                return Application.newObject(signature);
            case "android/content/Context->getContentResolver()Landroid/content/ContentResolver;":
                return vm.resolveClass("android/content/ContentResolver").newObject(signature);
            case "java/util/UUID->toString()Ljava/lang/String;":
                return new StringObject(vm, dvmObject.getValue().toString());
            case "java/lang/String->replaceAll(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;":
                StringObject s1 = vaList.getObjectArg(0);
                StringObject s2 = vaList.getObjectArg(1);
                assert s1 != null;
                assert s2 != null;
                return new StringObject(vm, dvmObject.getValue().toString().replaceAll(s1.getValue(), s2.getValue()));
            case "GetMethodID(android/view/Display->getRealMetrics(Landroid/util/DisplayMetrics;)V":
                System.out.println("getRealMetricsCalled");
                return  vm.resolveClass("android/util/DisplayMetrics").newObject(signature);

        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    public Module getModule() {
        return module;
    }

    public void deviceInfo2(){
//        inlineHookSub_11368();
//        inlineHookSub_1150C();
        inlineHookSub_113FC();
        List<Object> params = initParams(10);
        // custom = null;
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);
        params.add(vm.addLocalObject(context));
        params.add(1631598658287L);
        Number number = module.callFunction(emulator, 0xE3D5, params.toArray())[0];
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println("[DEVICE INFO]: " + result);
    }

    public void deviceInfo3(){
        inlineHookSub_114B0();
        List<Object> params = initParams(10);
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);
        params.add(vm.addLocalObject(context));
        params.add(1633751637326L);
        StringObject list_id = new StringObject(vm, "flj75qai");
        params.add(vm.addLocalObject(list_id));
        Number number = module.callFunction(emulator, 0xF16D, params.toArray())[0];
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println("[DEVICE INFO]: " + result);
    }

    public void generateData(){
        List<Object> params = initParams(10);
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);
        params.add(vm.addLocalObject(context));
        params.add(vm.addLocalObject(new StringObject(vm, "")));
        params.add(vm.addLocalObject(new StringObject(vm, "ChUG8WFn1bcwFWQoZVggAg==")));
        params.add(vm.addLocalObject(new StringObject(vm, "")));
        params.add(vm.addLocalObject(new StringObject(vm, "/storage/emulated/0")));
        params.add(vm.addLocalObject(new StringObject(vm, "start_by_user=true&app_type=&app_version=5.72.0&device_id=&clipboard_md5=&commitid=1acda8f5b30a7ccd69be847291cdcf70044bbb48&uuid=b1ccbd5d-9829-4382-961a-4af03e16b53f&scene=1&imei_shown=false&instrumentation_chain=android.app.Instrumentation&known_device=1&oaid=&version=33&wallpaper_md5=&sn_1=&sn_2=&sn_3=FA6BE0300728&app_list_info=com.xingin.xhs%3A1632727495529%3Bcom.tencent.mm%3A1617355401602%3Bcom.sankuai.meituan.takeoutnew%3A1593559150728%3Bnet.peerproxy.peerproxy%3A1634094158152%3Brca.rc.tvtaobao%3A1617872004887%3Bcom.tencent.android.qqdownloader%3A1587573301175%3Bjp.co.sumzap.pj0007%3A1587572912890%3Bcom.alimama.moon%3A1616564149655%3Bcom.YostarJP.BlueArchive%3A1628000590410%3Bcom.shizhuang.duapp%3A1618465546730%3Bjust.trust.me%3A1587502139152%3Bcom.xunmeng.pinduoduo%3A1632910320397%3Bcom.example.seccon2015.rock_paper_scissors%3A1587510080102%3Bcom.nikedlab.netcat%3A1633682108408%3Bio.va.exposed%3A1587502128997%3Bcom.ss.android.ugc.aweme%3A1623038152345%3Bcom.topjohnwu.magisk%3A1587146289063%3Bcom.example.xposed%3A1628756771320%3Bcom.yztc.studio.plugin%3A1629361740734%3Bcom.example.devicechange%3A1629279330313%3Bcom.example.xhsxposedplugin%3A1629273460201%3Bcom.tunnelworkshop.postern%3A1621932938933%3Bcom.centown.proprietor%3A1608633490223%3Bcom.guoshi.httpcanary%3A1632898307387%3Bctrip.android.view%3A1631347095105%3Bcom.qooapp.qoohelper%3A1587570534023%3Bcom.joaomgcd.autoinput%3A1634022131867%3Blv.id.dm.airplanemhx%3A1634023429625%3Btw.sonet.princessconnect%3A1587088151063%3Bcom.taobao.taobao%3A1616486965056%3Bin.zhaoj.shadowsocksrr%3A1587670545721%3B&app_list_all=1&input_mathod=com.google.android.inputmethod.latin&ringtone=%E7%A6%85&alarm=%E6%B5%81%E5%8A%A8&notification=%E9%93%83%E5%A3%B0&instrumentation=android.app.Instrumentation&kernelVersion=&brightness=144&simState=0&totalmemory=3945046016&availablememory=2231332864&totalcapacity=26109874176&availablecapacity=13709512704&imei_permission=-1&net_type=WIFI&ip_list=fe80%3A%3Ab89b%3Aa8ff%3Afe4d%3Af7f1%25dummy0%3Bfe80%3A%3Aae37%3A43ff%3Afea3%3A3a0c%25wlan0%3B192.168.120.5%3B&fk_result={%22vInfo%22%3A%7B%22exits%22%3A0%2C%22id%22%3A%22%22%7D,%22antInfo%22%3A%7B%22exits%22%3A0%7D}&machine_arch=ARM&arp_info=192.168.120.254%7C0x1%7C0x2%7C08%3A68%3A8d%3A64%3A98%3A01%7C*%7Cwlan0%7C%3B192.168.121.239%7C0x1%7C0x2%7Cf2%3Aac%3A2a%3Ae7%3A24%3A0a%7C*%7Cwlan0%7C%3B&development_enabled=1&adb_enabled=1&user_phonename=Pixel&process_id=10101&psno=&mediaDrm=0%3A4143333734334133334130430000000000000000000000000000000000000000%7CGoogle%7C14.0.0%7CWidevine+CDM%3B&cid_inner=&cid=&input_device=6%7Csynaptics_dsxv26%7C954faadc99bb5a7c1d0537b923e0490c90b47e98%7C4355%3B&wifi_config=&connected_wifi=%3Cunknown+ssid%3E|02%3A00%3A00%3A00%3A00%3A00|-53&foreground=true&secure_lock=1&currentTime=1634194801581")));
        params.add(1634194801708L);

        Number number = module.callFunction(emulator, 0x247D, params.toArray())[0];
//        Number number = module.callFunction(emulator, 0x4354D, params.toArray())[0];
        System.out.println(number);
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println("[GENERATE DATA]: " + result);
    }

    public void UserEnv(){
        List<Object> params = initParams(10);
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);
        params.add(vm.addLocalObject(context));
        params.add(380);
//        Number number = module.callFunction(emulator, 0x40B9D, params.toArray())[0];
//        Module module = dm_userEnv.getModule();
        Number number = module.callFunction(emulator, "GetUserEnvStr", params.toArray())[0];
//        UnidbgPointer ptr = UnidbgPointer.pointer(emulator, number.intValue());
//        String target = ptr.getPointer(0).getString(0, "utf-8");
//        System.out.println("target: " + target);
//        System.out.println(number.intValue());
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println("[GENERATE DATA]: " + result);
    }

    public void keyProcess() throws IOException {
        System.out.println("start");
//        List<Object> params = initParams(10);
        List<Object> params = new ArrayList<>(7);
        byte[] key = new byte[]{
                (byte)0x5b, (byte)0x53, (byte)0xb2, (byte)0xf0, (byte)0xea, (byte)0x9f, (byte)0x0c, (byte)0xc8, (byte)0xe1, (byte)0x6b, (byte)0x9f, (byte)0x00, (byte)0x4d, (byte)0xad, (byte)0x0b, (byte)0xeb,
                (byte)0x1a, (byte)0xec, (byte)0xd0, (byte)0xe3, (byte)0xd7, (byte)0x51, (byte)0x82, (byte)0x7e, (byte)0x5f, (byte)0x12, (byte)0x8e, (byte)0x34, (byte)0x41, (byte)0xd8, (byte)0x0b, (byte)0xa0
        };

        byte[] pub_key = new byte[]{
                (byte)0xc2, (byte)0x1d, (byte)0xa1, (byte)0xb2, (byte)0xc6, (byte)0x62, (byte)0x36, (byte)0xe7, (byte)0xca, (byte)0xdc, (byte)0xf8, (byte)0x2c, (byte)0x04, (byte)0xb3, (byte)0xdd, (byte)0x18,
                (byte)0xa4, (byte)0x1f, (byte)0xa9, (byte)0xfe, (byte)0x99, (byte)0xe2, (byte)0x33, (byte)0x88, (byte)0xde, (byte)0x4a, (byte)0xb4, (byte)0x66, (byte)0x36, (byte)0xe4, (byte)0xdd, (byte)0x02,
                (byte)0x96, (byte)0x72, (byte)0x5d, (byte)0x0a, (byte)0x69, (byte)0x9e, (byte)0x58, (byte)0x54, (byte)0x4f, (byte)0xdd, (byte)0xdd, (byte)0xcf, (byte)0x25, (byte)0x19, (byte)0x86, (byte)0x23,
                (byte)0x0d, (byte)0x03, (byte)0xd7, (byte)0x45, (byte)0x1a, (byte)0x25, (byte)0xeb, (byte)0x5c, (byte)0x62, (byte)0x32, (byte)0xc9, (byte)0x04, (byte)0xcd, (byte)0xc7, (byte)0xbb, (byte)0x6e,
                (byte)0x4c, (byte)0xb9, (byte)0xf1, (byte)0x81, (byte)0x26, (byte)0xfb, (byte)0x6e, (byte)0x83, (byte)0xf1, (byte)0xa5, (byte)0x9b, (byte)0x5d, (byte)0xa1, (byte)0x49, (byte)0x17, (byte)0x83,
                (byte)0x8e, (byte)0x82, (byte)0x93, (byte)0x8e, (byte)0x71, (byte)0x08, (byte)0x8c, (byte)0x68, (byte)0x35, (byte)0x6e, (byte)0xa0, (byte)0x62, (byte)0xa7, (byte)0x3d, (byte)0x83, (byte)0xee,
                (byte)0x44, (byte)0xdb, (byte)0x69, (byte)0x8f, (byte)0xa6, (byte)0xca, (byte)0xb3, (byte)0x56, (byte)0xe0, (byte)0x88, (byte)0x1d, (byte)0x68, (byte)0xb1, (byte)0x3a, (byte)0xa8, (byte)0xf8,
                (byte)0x75, (byte)0x43, (byte)0xf0, (byte)0xd7, (byte)0x21, (byte)0xcd, (byte)0xd9, (byte)0xb6, (byte)0x87, (byte)0xa0, (byte)0x17, (byte)0x5e, (byte)0xe0, (byte)0x30, (byte)0x47, (byte)0x9b,
        };

        byte[] x = new byte[]{1, 0, 1};
        params.add(key);
        params.add(32);

        params.add(pub_key);
        params.add(128);

        params.add(x);
        params.add(3);

        MemoryBlock memoryBlock2 = emulator.getMemory().malloc(128, false);
        UnidbgPointer output_buffer=memoryBlock2.getPointer();
        params.add(output_buffer);
        emulator.traceCode(module.base, module.base + module.size);
        Number number = module.callFunction(emulator, 0x42B71, params.toArray())[0];
        Inspector.inspect(output_buffer.getByteArray(0, 128), "output");
    }

    public void sub_3DCB8() throws IOException {
        byte[] data = new byte[]{
                (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x6f, (byte)0x00, (byte)0x0f, (byte)0x01, (byte)0x01, (byte)0x0c, (byte)0x46, (byte)0x41, (byte)0x36, (byte)0x42, (byte)0x45,
                (byte)0x30, (byte)0x33, (byte)0x30, (byte)0x30, (byte)0x37, (byte)0x32, (byte)0x38, (byte)0x00, (byte)0x09, (byte)0x01, (byte)0x02, (byte)0x06, (byte)0x67, (byte)0x6f, (byte)0x6f, (byte)0x67,
                (byte)0x6c, (byte)0x65, (byte)0x00, (byte)0x0b, (byte)0x01, (byte)0x03, (byte)0x08, (byte)0x73, (byte)0x61, (byte)0x69, (byte)0x6c, (byte)0x66, (byte)0x69, (byte)0x73, (byte)0x68, (byte)0x00,
                (byte)0x08, (byte)0x01, (byte)0x04, (byte)0x05, (byte)0x50, (byte)0x69, (byte)0x78, (byte)0x65, (byte)0x6c, (byte)0x00, (byte)0x09, (byte)0x01, (byte)0x05, (byte)0x06, (byte)0x47, (byte)0x6f,
                (byte)0x6f, (byte)0x67, (byte)0x6c, (byte)0x65, (byte)0x00, (byte)0x0b, (byte)0x01, (byte)0x06, (byte)0x08, (byte)0x73, (byte)0x61, (byte)0x69, (byte)0x6c, (byte)0x66, (byte)0x69, (byte)0x73,
                (byte)0x68, (byte)0x00, (byte)0x12, (byte)0x01, (byte)0x07, (byte)0x0f, (byte)0x50, (byte)0x50, (byte)0x52, (byte)0x31, (byte)0x2e, (byte)0x31, (byte)0x38, (byte)0x30, (byte)0x36, (byte)0x31,
                (byte)0x30, (byte)0x2e, (byte)0x30, (byte)0x30, (byte)0x39, (byte)0x00, (byte)0x2c, (byte)0x01, (byte)0x08, (byte)0x29, (byte)0x50, (byte)0x50, (byte)0x52, (byte)0x31, (byte)0x2e, (byte)0x31,
                (byte)0x38, (byte)0x30, (byte)0x36, (byte)0x31, (byte)0x30, (byte)0x2e, (byte)0x30, (byte)0x30, (byte)0x39, (byte)0x2f, (byte)0x34, (byte)0x38, (byte)0x39, (byte)0x38, (byte)0x39, (byte)0x31,
                (byte)0x31, (byte)0x3a, (byte)0x75, (byte)0x73, (byte)0x65, (byte)0x72, (byte)0x2f, (byte)0x72, (byte)0x65, (byte)0x6c, (byte)0x65, (byte)0x61, (byte)0x73, (byte)0x65, (byte)0x2d, (byte)0x6b,
                (byte)0x65, (byte)0x79, (byte)0x73, (byte)0x00, (byte)0x04, (byte)0x01, (byte)0x09, (byte)0x01, (byte)0x39, (byte)0x00, (byte)0x04, (byte)0x01, (byte)0x0a, (byte)0x01, (byte)0x1c, (byte)0x00,
                (byte)0x07, (byte)0x01, (byte)0x0b, (byte)0x04, (byte)0x5b, (byte)0x4e, (byte)0x7e, (byte)0xc8, (byte)0x00, (byte)0x07, (byte)0x01, (byte)0x0c, (byte)0x04, (byte)0x49, (byte)0x5c, (byte)0x78,
                (byte)0x00, (byte)0x00, (byte)0x0b, (byte)0x01, (byte)0x0d, (byte)0x08, (byte)0x00, (byte)0x01, (byte)0x40, (byte)0xc4, (byte)0xd3, (byte)0x70, (byte)0x24, (byte)0xc1, (byte)0x00, (byte)0x0b,
                (byte)0x01, (byte)0x0e, (byte)0x08, (byte)0x00, (byte)0x01, (byte)0x40, (byte)0xc4, (byte)0xd3, (byte)0x70, (byte)0x24, (byte)0xc1, (byte)0x00, (byte)0x0b, (byte)0x01, (byte)0x0f, (byte)0x08,
                (byte)0x00, (byte)0x35, (byte)0x26, (byte)0x89, (byte)0x08, (byte)0x18, (byte)0x20, (byte)0x35, (byte)0x00, (byte)0x03, (byte)0x01, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x01,
                (byte)0x11, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x01, (byte)0x12, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x03, (byte)0x01, (byte)0x13, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x01,
                (byte)0x14, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x01, (byte)0x15, (byte)0x00, (byte)0x00, (byte)0x0a, (byte)0x01, (byte)0x16, (byte)0x07, (byte)0x55, (byte)0x4e, (byte)0x4b, (byte)0x4e,
                (byte)0x4f, (byte)0x57, (byte)0x4e, (byte)0x00, (byte)0x03, (byte)0x01, (byte)0x17, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x01, (byte)0x18, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x01,
                (byte)0x19, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x01, (byte)0x1a, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x01, (byte)0x1b, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x01,
                (byte)0x1c, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x0b, (byte)0x01, (byte)0x1e, (byte)0x08, (byte)0x5e, (byte)0xdb, (byte)0xa3, (byte)0x27, (byte)0x2d, (byte)0x34, (byte)0x25, (byte)0x95,
                (byte)0x00, (byte)0x04, (byte)0x01, (byte)0x1f, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x0b, (byte)0x01, (byte)0x20, (byte)0x08, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x7b, (byte)0xe3,
                (byte)0xd7, (byte)0x39, (byte)0x5d, (byte)0x00, (byte)0x0b, (byte)0x01, (byte)0x21, (byte)0x08, (byte)0x18, (byte)0xf4, (byte)0x25, (byte)0xeb, (byte)0x08, (byte)0x90, (byte)0x36, (byte)0xb8,
                (byte)0x00, (byte)0x23, (byte)0x01, (byte)0x22, (byte)0x20, (byte)0x30, (byte)0x33, (byte)0x36, (byte)0x66, (byte)0x35, (byte)0x36, (byte)0x65, (byte)0x32, (byte)0x39, (byte)0x35, (byte)0x38,
                (byte)0x65, (byte)0x34, (byte)0x31, (byte)0x61, (byte)0x62, (byte)0x39, (byte)0x36, (byte)0x35, (byte)0x64, (byte)0x37, (byte)0x38, (byte)0x63, (byte)0x32, (byte)0x63, (byte)0x31, (byte)0x30,
                (byte)0x33, (byte)0x32, (byte)0x39, (byte)0x61, (byte)0x30

        };
        byte[] gzipData = gzipProcess.encode(data);
        List<Object> params = initParams(10);
        params.add(new ByteArray(vm, "pdd_aes_180121_1".getBytes()));
        params.add(new ByteArray(vm, gzipData));
        params.add(gzipData.length);
        params.add(new ByteArray(vm, data));


        Number number = module.callFunction(emulator, 0x3DCB8, params.toArray())[0];
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println("[DEVICE INFO]: " + result);
    }

    private void inlineHookSub_11368(){
        int offset = 0x11368;
        IHookZz hookZz = HookZz.getInstance(emulator);
        hookZz.instrument(module.base + offset + 1, new InstrumentCallback<Arm32RegisterContext>() {
            @Override
            public void dbiCall(Emulator<?> emulator, Arm32RegisterContext ctx, HookEntryInfo info) {
                String args1 = Integer.toHexString(ctx.getIntArg(0));
                String index = Integer.toHexString(ctx.getIntArg(1));
                int length = ctx.getIntArg(3);
                String args2 = Integer.toHexString(ctx.getIntArg(2));
                System.out.println("[address1] " + args1 + " [address 2] " + args2 + " [index] " + index + " [length] " + length  + " was called from " + emulator.<RegisterContext>getContext().getLRPointer());
            }
        });
    }

    private void inlineHookSub_1150C(){
        int offset = 0x1150C;
        IHookZz hookZz = HookZz.getInstance(emulator);
        hookZz.instrument(module.base + offset + 1, new InstrumentCallback<Arm32RegisterContext>() {
            @Override
            public void dbiCall(Emulator<?> emulator, Arm32RegisterContext ctx, HookEntryInfo info) {
                String args1 = Integer.toHexString(ctx.getIntArg(0));
                String args2 = Integer.toHexString(ctx.getIntArg(1));
                String index = Integer.toHexString(ctx.getIntArg(2));
                int length = ctx.getIntArg(3);
                System.out.println("[address1] " + args1 + " [address 2] " + args2 + " [index] " + index + " [length] " + length  + " was called from " + emulator.<RegisterContext>getContext().getLRPointer());
            }
        });
    }

    private void inlineHookSub_114B0(){
        int offset = 0x114B0;
        IHookZz hookZz = HookZz.getInstance(emulator);
        hookZz.instrument(module.base + offset + 1, new InstrumentCallback<Arm32RegisterContext>() {
            @Override
            public void dbiCall(Emulator<?> emulator, Arm32RegisterContext ctx, HookEntryInfo info) {
                int args1 =  ctx.getIntArg(0);
                int args2 = ctx.getIntArg(1);
                int args3 = ctx.getIntArg(2);
                int args4 = ctx.getIntArg(3);
                int args5 = ctx.getIntArg(4);
                if(args2 > 10 ){
//                    System.out.println(Arrays.toString(ctx.getPointerArg(4).getByteArray(0, 8)));
                }
                System.out.println("[args1] " + args1 + " [args2] " + args2 + " [args3] " + args3 + " [args4] " + args4  + " [arags5] " + args5 + " was called from " + emulator.<RegisterContext>getContext().getLRPointer());
            }
        });
    }

    private void inlineHookSub_113FC(){
        int offset = 0x113FC;
        IHookZz hookZz = HookZz.getInstance(emulator);
        hookZz.instrument(module.base + offset + 1, new InstrumentCallback<Arm32RegisterContext>() {
            @Override
            public void dbiCall(Emulator<?> emulator, Arm32RegisterContext ctx, HookEntryInfo info) {
                String args1 = Integer.toHexString(ctx.getIntArg(0));
                String index = Integer.toHexString(ctx.getIntArg(1));
                String args2 = Integer.toHexString(ctx.getIntArg(2));
                String args3 = Integer.toHexString(ctx.getIntArg(3));
//                System.out.println(Arrays.toString(ctx.getPointerArg(2).getByteArray(1, 8)));
                int length = ctx.getIntArg(4);
                System.out.println("[address1] " + args1 + " [address 2] " + args2 + " [address 3] " + args3 + " [index] " + index + " [length] " + length  + " was called from " + emulator.<RegisterContext>getContext().getLRPointer());
            }
        });
    }


    private void inlineHookSub_3DCB8(){
        int offset = 0x3DCB8;
        IHookZz hookZz = HookZz.getInstance(emulator);
        hookZz.instrument(module.base + offset + 1, new InstrumentCallback<Arm32RegisterContext>() {
            @Override
            public void dbiCall(Emulator<?> emulator, Arm32RegisterContext ctx, HookEntryInfo info) {
                String args1 = Integer.toHexString(ctx.getIntArg(0));
                String index = Integer.toHexString(ctx.getIntArg(1));
                String args2 = Integer.toHexString(ctx.getIntArg(2));
                String args3 = Integer.toHexString(ctx.getIntArg(3));
//                System.out.println(Arrays.toString(ctx.getPointerArg(2).getByteArray(1, 8)));
                int length = ctx.getIntArg(4);
                System.out.println("[address1] " + args1 + " [address 2] " + args2 + " [address 3] " + args3 + " [index] " + index + " [length] " + length  + " was called from " + emulator.<RegisterContext>getContext().getLRPointer());
            }
        });
    }

    private void inlineHookSub_42B70(){
        int offset = 0x42B70;
        IHookZz hookZz = HookZz.getInstance(emulator);
        hookZz.instrument(module.base + offset + 1, new InstrumentCallback<Arm32RegisterContext>() {
            @Override
            public void dbiCall(Emulator<?> emulator, Arm32RegisterContext ctx, HookEntryInfo info) {
                String args0 = Integer.toHexString(ctx.getIntArg(0));
                String args1 = Integer.toHexString(ctx.getIntArg(1));
                String args2 = Integer.toHexString(ctx.getIntArg(2));
                String args3 = Integer.toHexString(ctx.getIntArg(6));
                try{
                    ByteProcess.hexDump(vm.getObject(ctx.getIntArg(6)).getValue().toString().getBytes(), "args1");
//                    ByteProcess.hexDump(vm.getObject(ctx.getIntArg(2)).getValue().toString().getBytes(), "args3");
                }catch (Exception e){
                    System.out.println("[Exception]" + e);
                }
//                System.out.println(Arrays.toString(ctx.getPointerArg(2).getByteArray(1, 8)));
                System.out.println("[address1] " + args0 + " [address 2] " + args1 + " [address 3] " + args2 + " [index] " + args3 + " was called from " + emulator.<RegisterContext>getContext().getLRPointer());
            }
        });
    }

    private void inlineHookSub_9DA38(){
        int offset = 0x9DA38;
        IHookZz hookZz = HookZz.getInstance(emulator);
        hookZz.instrument(module.base + offset + 1, new InstrumentCallback<Arm32RegisterContext>() {
            @Override
            public void dbiCall(Emulator<?> emulator, Arm32RegisterContext ctx, HookEntryInfo info) {
                String args0 = Integer.toHexString(ctx.getIntArg(0));
                String args1 = Integer.toHexString(ctx.getIntArg(1));
                String args2 = Integer.toHexString(ctx.getIntArg(2));
                String args3 = Integer.toHexString(ctx.getIntArg(3));
                try{
//                    ByteProcess.hexDump(vm.getObject(ctx.getIntArg(1)).getValue().toString().getBytes(), "args1");
//                    ByteProcess.hexDump(vm.getObject(ctx.getIntArg(2)).getValue().toString().getBytes(), "args3");
                    System.out.println(ctx.getPointerArg(1).getString(0));
                }catch (Exception e){
                    System.out.println("[Exception]" + e);
                }
//                System.out.println(Arrays.toString(ctx.getPointerArg(2).getByteArray(1, 8)));
//                System.out.println("[address1] " + args0 + " [address 2] " + args1 + " [address 3] " + args2 + " [index] " + args3 + " was called from " + emulator.<RegisterContext>getContext().getLRPointer());
            }
        });
    }
}
