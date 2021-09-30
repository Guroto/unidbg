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
import com.github.unidbg.utils.Inspector;
import com.kero.common.BaseAndroidEmulator;
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
    public static String[] soList = new String[]{"libc++_shared.so", "libUserEnv.so", "libpdd_secure.so"};
    public static String baseSoPath = "unidbg-android/src/test/resources/example_binaries/armeabi-v7a/";
    public static String APKPath = "unidbg-android/src/test/resources/apks/pdd.apk";
    private final Logger logger = Logger.getLogger("LoggingDemo");
    static boolean  verbose = true;

    public PddSecure(){
        super(processName, APKPath, baseSoPath, soList, verbose);
        DalvikModule dm_userEnv = vm.loadLibrary(new File(baseSoPath + "libUserEnv.so"), true);
        dm_userEnv.callJNI_OnLoad(emulator);

        // hook
        PddSecureHook myHook =  new PddSecureHook(emulator, module);
        myHook.hook(0x113FC, 0x114A6, 100);
    }

    public static void main(String[] args) throws IOException {
        Logger.getLogger(String.valueOf(DalvikVM.class)).setLevel(Level.DEBUG);
        Logger.getLogger(String.valueOf(BaseVM.class)).setLevel(Level.DEBUG);
//        Logger.getLogger(String.valueOf(SystemPropertyHook.class)).setLevel(Level.DEBUG);
        PddSecure pddSecure = new PddSecure();
        pddSecure.deviceInfo2();
//        pddSecure.sub_3DCB8();
    }

    @Override
    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature){
        switch (signature){
            case "android/provider/Settings$Secure->ANDROID_ID:Ljava/lang/String;":
                return new StringObject(vm, "android_id");
        }
        return super.getStaticObjectField(vm, dvmClass, signature);

    }

    @Override
    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, DvmMethod dvmMethod, VarArg varArg) {
        switch (dvmMethod.getSignature()){
            case "android/provider/Settings$Secure->getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;":{
                String arg = (String) varArg.getObjectArg(1).getValue();
                System.out.println("[Settings$Secure] -> [getString]" + arg);
                return new StringObject(vm, "");
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
                return new StringObject(vm, "");
            case "java/util/UUID->randomUUID()Ljava/util/UUID;":
                return vm.resolveClass("java/util/UUID").newObject(UUID.randomUUID());
        }

        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public void callStaticVoidMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList){
        switch (signature){
            case "com/tencent/mars/xlog/PLog->i(Ljava/lang/String;Ljava/lang/String;)V":
                System.out.println("[ARGS]: " + vaList.getObjectArg(1).getValue());
                System.out.println("[ARGS]: " + vaList.getObjectArg(0).getValue());
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
            case "android/content/Context->getSystemService(Ljava/lang/String;)Ljava/lang/Object;":
                String arg = varArg.getObjectArg(0).getValue().toString();
                System.out.println("[getSystemService][ARG-0]: " + arg);
                return vm.resolveClass("android/telephony/TelephonyManager").newObject(signature);
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
}
