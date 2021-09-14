package com.xunmeng.pdd;

import com.github.unidbg.Module;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.kero.common.BaseAndroidEmulator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
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
    }

    public static void main(String[] args) {
        Logger.getLogger(String.valueOf(DalvikVM.class)).setLevel(Level.DEBUG);
        Logger.getLogger(String.valueOf(BaseVM.class)).setLevel(Level.DEBUG);
        PddSecure pddSecure = new PddSecure();
        pddSecure.deviceInfo2();
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
        List<Object> params = initParams(10);
        // custom = null;
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);
        params.add(vm.addLocalObject(context));
        params.add(1631598658287L);
        Number number = module.callFunction(emulator, 0xE3D5, params.toArray())[0];
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println("[DEVICE INFO]: " + result);
    }
}
