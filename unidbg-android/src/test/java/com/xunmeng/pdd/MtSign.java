package com.xunmeng.pdd;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.debugger.Debugger;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.ApplicationInfo;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.linux.android.dvm.wrapper.DvmInteger;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.linux.file.SimpleFileIO;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * url quection (not url)
 */
public class MtSign extends AbstractJni implements IOResolver {
    private static  AndroidEmulator emulator =null;
    private static  VM vm =null;
    private static  Module module =null;
    //private final DvmClass Collector;
    private static MtSign mtSign =null;
    private String apkPath ="";

    MtSign() throws FileNotFoundException {

        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName("com.meituan").build(); // 创建模拟器实例，要模拟32位或者64位，在这里区分
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析

        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/base.apk")); // 创建Android虚拟机
        emulator.getSyscallHandler().addIOResolver(this);
        vm.setVerbose(true); // 设置是否打印Jni调用细节
        vm.setJni(this);
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/resources/example_binaries/armeabi-v7a/libmtguard.so"), true);

        module = dm.getModule(); //

        dm.callJNI_OnLoad(emulator);
        Debugger debugger = emulator.attach();
        // 添加断点
//        debugger.addBreakPoint(module.base + 0x9c814);
//        debugger.addBreakPoint(module.base + 0x9c874);
//        debugger.addBreakPoint(module.base + 0x9c8a);
//        debugger.addBreakPoint(module.base + 0x9c8e6);
//        debugger.addBreakPoint(module.base + 0x9c984);
//        debugger.addBreakPoint(module.base + 0x9c98c);
//        debugger.addBreakPoint(module.base + 0x9c9d2);
//        debugger.addBreakPoint(module.base + 0x9ca1e);
//        /debugger.addBreakPoint(module.base + 0x9cc1e);
//        debugger.addBreakPoint(module.base + 0x627c0);
//        emulator.traceWrite(module.base, module.base + module.size);
//        emulator.traceRead(module.base, module.base + module.size);
//        emulator.traceCode(module.base, module.base+module.size);

//        Collector = vm.resolveClass("com/meituan/android/common/mtguard/NBridge$SIUACollector");
//        Debugger debugger = emulator.attach();
        // SetByteArrayRegion
//        debugger.addBreakPoint(module.base + 0x67105);
        // memcpy inline
//        debugger.addBreakPoint(module.base + 0xa7b96);

        // 第一层的trace，发现结果来自memcpy
//        emulator.traceWrite(0x4038228c, 0x4038228c + 0x1fa);

        // 第二层trace，查看memcpy的source是谁写进去的
//        emulator.traceWrite(0x403be000, 0x403be000 + 0x1fa);

        // 第三层trace，查看realloc的source是谁写进去的
        //emulator.traceWrite(0x40327000, 0x40327000 + 0x1fa);
//        call111();
    }



    public void call111(){
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        list.add(111);
        DvmObject<?> obj = vm.resolveClass("java/lang/object").newObject(null);
        vm.addLocalObject(obj);
        ArrayObject myobject = new ArrayObject(obj);
        vm.addLocalObject(myobject);
        // 完整的参数2
        list.add(vm.addLocalObject(myobject));
        module.callFunction(emulator, 0x5a38d, list.toArray());
    };

    public  String call501() throws FileNotFoundException {

        // 填入自己的path
//        String traceFile = "unidbg-android/src/test/resources/encode2.txt";
//        PrintStream traceStream = new PrintStream(new FileOutputStream(traceFile), true);
//        emulator.traceCode(module.base, module.base+module.size).setRedirect(traceStream);
        System.out.println("lilac call mt");
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        list.add(501);
        DvmInteger input1 = DvmInteger.valueOf(vm, 1);
        vm.addLocalObject(input1);
        DvmObject<?> context =vm.resolveClass("android/content/Context").newObject(null);
//        vm.addLocalObject(context);
        list.add(vm.addLocalObject(new ArrayObject(input1,context)));
        Number number =module.callFunction(emulator,0x5a38d,list.toArray())[0];
        DvmObject<?> resultdvm = ((DvmObject[])((ArrayObject)vm.getObject(number.intValue())).getValue())[0];
        byte[] result =(byte[]) resultdvm.getValue();
        String base64Str = Base64.getEncoder().encodeToString(result);
//        String base64Str = bytesToHexString(result);
        System.out.println(result);
        return base64Str;

    }

    public String callMt(){
        System.out.println("lilac call mt");
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        list.add(203);
        StringObject input2_1 = new StringObject(vm, "9b69f861-e054-4bc4-9daf-d36ae205ed3e");
        ByteArray input2_2 = new ByteArray(vm, "GET /volga/api/v5/trip/poi/external/filters __reqTraceID=8bd9c87b-b28b-4907-8294-bcff63f11abf&cateId=2&ci=50&cityId=50&client=android&cn_pt=RN&gps_cityid=50&hotelCustomGpsStatus=1&innerChannel=3&isPrefetch=true&lat=30.335422011987475&lng=120.11954142362069&msid=3525310838386131627018366973&osversion=8.1.0&selectedCityId=50&source=mt&strategy=0&userid=-1&utm_campaign=AgroupBgroupC0E0Ghomepage_category7_296__a1__c-1024&utm_content=352531083838613&utm_medium=android&utm_source=wandoujia&utm_term=1100090405&uuid=0000000000000B37B56A20AB14930A912107A2301D7E2A162072576629078623&version=11.9.405&version_name=11.9.405".getBytes(StandardCharsets.UTF_8));//GET /volga/api/v5/trip/poi/external/filters __reqTraceID=8bd9c87b-b28b-4907-8294-bcff63f11abf&cateId=2&ci=50&cityId=50&client=android&cn_pt=RN&gps_cityid=50&hotelCustomGpsStatus=1&innerChannel=3&isPrefetch=true&lat=30.335422011987475&lng=120.11954142362069&msid=3525310838386131627018366973&osversion=8.1.0&selectedCityId=50&source=mt&strategy=0&userid=-1&utm_campaign=AgroupBgroupC0E0Ghomepage_category7_296__a1__c-1024&utm_content=352531083838613&utm_medium=android&utm_source=wandoujia&utm_term=1100090405&uuid=0000000000000B37B56A20AB14930A912107A2301D7E2A162072576629078623&version=11.9.405&version_name=11.9.405
        DvmInteger input2_3 = DvmInteger.valueOf(vm, 2);
        vm.addLocalObject(input2_1);
        vm.addLocalObject(input2_2);
        vm.addLocalObject(input2_3);
        // 完整的参数2
        list.add(vm.addLocalObject(new ArrayObject(input2_1, input2_2, input2_3)));
        Number number = module.callFunction(emulator, 0x5a38d, list.toArray())[0];

        StringObject result = (StringObject) ((DvmObject[])((ArrayObject)vm.getObject(number.intValue())).getValue())[0];
        return result.getValue();
    };

    @Override
    public DvmObject<?> getObjectField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature){
            // 出问题了，说明我们的context不好用，Objection+Frida call看看是啥
            // 要确认继承关系
            case "com/meituan/android/common/mtguard/NBridge$SIUACollector->mContext:Landroid/content/Context;":{
                DvmClass cContext = vm.resolveClass("android/content/Context");
                DvmClass cContextWrapper = vm.resolveClass("android/content/ContextWrapper", cContext);
                return cContextWrapper.newObject(null);
//                return vm.resolveClass("com/sankuai/meituan/MeituanApplication", cContextWrapper).newObject(null);
            }
            case "android/content/res/Configuration->locale:Ljava/util/Locale;":{
                return vm.resolveClass("java/util/Locale").newObject(new Locale("chinese"));
            }
        }
        return super.getObjectField(vm, dvmObject, signature);
    }

    // 如果采用自定义的数据格式，那就麻烦不少了！
    @Override
    public DvmObject<?> allocObject(BaseVM vm, DvmClass dvmClass, String signature) {
        if ("java/lang/StringBuilder->allocObject".equals(signature)) {
            return dvmClass.newObject(new StringBuilder());
        }
        if ("java/text/SimpleDateFormat->allocObject".equals(signature)) {
            return dvmClass.newObject(new SimpleDateFormat());
        }
        if ("java/util/Date->allocObject".equals(signature)) {
            return dvmClass.newObject(new Date());
        }

        return super.allocObject(vm, dvmClass, signature);
    }

    @Override
    public void callVoidMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            case "java/lang/StringBuilder-><init>()V":{
//                vm.resolveClass("java/lang/StringBuilder").newObject(new StringBuilder());
            }
        }
    }

    // result:bullhead|LGE|google|Nexus 5X|arm64-v8a|-|bullhead|bullhead|bullhead|
    // bullhead|vpea3.mtv.corp.google.com|OPM7.181205.001|8.1.0|27|zh|CN|release-keys
    // |google/bullhead/bullhead:8.1.0/OPM7.181205.001/5080180:user/release-keys|user
    // |bullhead-user 8.1.0 OPM7.181205.001 5080180 release-keys|1|0|
//    @Override
//    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature) {
//        switch (signature) {
//            case "android/os/Build->BOARD:Ljava/lang/String;":
//                return new StringObject(vm, "bullhead");
//            case "android/os/Build->MANUFACTURER:Ljava/lang/String;":
//                return new StringObject(vm, "LGE");
//            case "android/os/Build->BRAND:Ljava/lang/String;":
//                return new StringObject(vm, "google");
//            case "android/os/Build->MODEL:Ljava/lang/String;":
//                return new StringObject(vm, "Nexus 5X");
//            case "android/os/Build->PRODUCT:Ljava/lang/String;":
//                return new StringObject(vm, "bullhead");
//            case "android/os/Build->HARDWARE:Ljava/lang/String;":
//                return new StringObject(vm, "bullhead");
//            case "android/os/Build->DEVICE:Ljava/lang/String;":
//                return new StringObject(vm, "bullhead");
//            case "android/os/Build->HOST:Ljava/lang/String;":
//                return new StringObject(vm, "vpea3.mtv.corp.google.com");
//            case "android/os/Build->ID:Ljava/lang/String;":
//                return new StringObject(vm, "OPM7.181205.001");
//            case "android/os/Build->TAGS:Ljava/lang/String;":
//                return new StringObject(vm, "release-keys");
//            case "android/os/Build->FINGERPRINT:Ljava/lang/String;":
//                return new StringObject(vm, "google/bullhead/bullhead:8.1.0/OPM7.181205.001/5080180:user/release-keys");
//            case "android/os/Build->TYPE:Ljava/lang/String;":
//                return new StringObject(vm, "user");
//
//        }
//        return super.getStaticObjectField(vm, dvmClass, signature);
//    }

    @Override
    public boolean callBooleanMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "android/content/pm/PackageManager->hasSystemFeature(Ljava/lang/String;)Z":
//                String key = (String) vaList.getObject(0).getValue();
                return true;
        }
        return super.callBooleanMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public int getStaticIntField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature){
            case "android/os/Build$VERSION->SDK_INT:I":
                return 27;
            case "android/content/pm/PackageManager->GET_SIGNATURES:I":
                return 64;
            case "android/content/pm/ApplicationInfo->FLAG_SYSTEM:I":
                return 1;
        }
        return super.getStaticIntField(vm, dvmClass, signature);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "com/meituan/android/common/mtguard/NBridge$SIUACollector->checkBuildAttribute(Ljava/lang/String;)Ljava/lang/String;": {
                DvmObject str = (DvmObject) Objects.requireNonNull(vaList.getObjectArg(0));
                if(str.equals("bullhead")){
                    return new StringObject(vm, "bullhead");
                }
                if(str.equals("LGE")){
                    return new StringObject(vm, "LGE");
                }
                return new StringObject(vm, str.toString());
            }
            case "java/lang/StringBuilder->append(Ljava/lang/String;)Ljava/lang/StringBuilder;":{
                StringBuilder str = (StringBuilder) dvmObject.getValue();
                StringObject val = vaList.getObjectArg(0);
                assert val != null;
                return vm.resolveClass("java/lang/StringBuilder").newObject(str.append(val.getValue()));
            }
            case "com/meituan/android/common/mtguard/NBridge$SIUACollector->getSysProp(Ljava/lang/String;)Ljava/lang/String;":{
                DvmObject key = (DvmObject) Objects.requireNonNull(vaList.getObjectArg(0));
                System.out.println(key);
                return new StringObject(vm, key.toString());
            }
            case "android/content/Context->getResources()Landroid/content/res/Resources;":{
                return vm.resolveClass("android/content/res/Resources").newObject(null);
            }
            case "android/content/res/Resources->getConfiguration()Landroid/content/res/Configuration;":{
                return vm.resolveClass("android/content/res/Configuration").newObject(null);
            }
            case "java/lang/StringBuilder->toString()Ljava/lang/String;":{
                StringBuilder str = (StringBuilder) dvmObject.getValue();
                return new StringObject(vm, str.toString());
            }
            case "java/text/SimpleDateFormat->format(Ljava/util/Date;)Ljava/lang/String;":{
                DvmObject<?> dateObject = vaList.getObjectArg(0);
                Date date = (Date) dateObject.getValue();
                SimpleDateFormat simpleDateFormat = ((SimpleDateFormat) dvmObject.getValue());
                String dateStr = simpleDateFormat.format(date);
                return new StringObject(vm, dateStr);
            }

            case "android/content/Context->getPackageCodePath()Ljava/lang/String;":{
                return new StringObject(vm, "/data/app/com.sankuai.meituan-TEfTAIBttUmUzuVbwRK1DQ==/base.apk");
            }
            case "android/content/pm/PackageManager->getPackagesForUid(I)[Ljava/lang/String;":{
                int num = vaList.getIntArg(0);
//                System.out.println("getPackagesForUid Num:"+num);


                switch (num){
                    case 10000: {
                        StringObject input2_1 = new StringObject(vm, "android.auto_generated_rro__");// 第一个参数是env
                        return  new ArrayObject(input2_1);
                    }
                    case 10001:{
                        StringObject input2_1 = new StringObject(vm, "com.sankuai.meituan");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10002:{
                        StringObject input2_1 = new StringObject(vm, "com.android.systemui.theme.dark");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10003:{
                        StringObject input2_1 = new StringObject(vm, "com.google.android.wfcactivation");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10004:{
                        StringObject input2_1 = new StringObject(vm, "com.google.android.carriersetup");// 第一个参数是env
                        return  new ArrayObject(input2_1);
                    }
                    case 10005:{
                        StringObject input2_1 = new StringObject(vm, "com.google.android.feedback");// 第一个参数是env
                        return  new ArrayObject(input2_1);
                    }
                    case 10006:{
                        StringObject input2_1 = new StringObject(vm, "com.android.statementservice");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10007:{
                        StringObject input2_1 = new StringObject(vm, "com.google.android.ims");// 第一个参数是env
                        return  new ArrayObject(input2_1);
                    }
                    case 10008:{
                        StringObject input2_1 = new StringObject(vm, "com.google.android.contacts");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10009:{
                        StringObject input2_1 = new StringObject(vm, "com.verizon.obdm_permissions");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10010:{
                        StringObject input2_1 = new StringObject(vm, "com.google.android.carrier");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10011:{
                        StringObject input2_1 = new StringObject(vm, "com.verizon.llkagent");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10012:{
                        StringObject input2_1 = new StringObject(vm, "com.google.android.dialer");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10013:{
                        StringObject input2_1 = new StringObject(vm, "com.android.systemui");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10014:{
                        StringObject input2_1 = new StringObject(vm, "com.android.sharedstoragebackup");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10015:{
                        StringObject input2_1 = new StringObject(vm, "com.android.providers.downloads.ui");// 第一个参数是env
                        StringObject input2_2 = new StringObject(vm, "com.android.mtp");// 第一个参数是env
                        StringObject input2_3 = new StringObject(vm, "com.android.providers.media");// 第一个参数是env
                        StringObject input2_4 = new StringObject(vm, "com.android.providers.downloads");// 第一个参数是env
                        return  new ArrayObject(input2_1, input2_2, input2_3 ,input2_4);
                    }
                    case 10016:{
                        StringObject input2_1 = new StringObject(vm, "com.android.musicfx");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10017:{
                        StringObject input2_1 = new StringObject(vm, "com.android.vzwomatrigger");// 第一个参数是env
                        return  new ArrayObject(input2_1);

                    }
                    case 10018:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.helprtc");// 第一个参数是env
                        return  new ArrayObject(input0);

                    }
                    case 10019:{
                        StringObject input0 = new StringObject(vm, "com.android.providers.contacts");
                        StringObject input1 = new StringObject(vm, "com.android.calllogbackup");
                        StringObject input2 = new StringObject(vm, "com.android.providers.userdictionary");
                        StringObject input3 = new StringObject(vm, "com.android.providers.blockednumber");
                        return  new ArrayObject(input0,input1,input2,input3);
                    }
                    case 10020:{
                        StringObject input0 = new StringObject(vm, "com.google.android.tag");
                        return  new ArrayObject(input0);
                    }
                    case 10021:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.wallpaper");
                        return  new ArrayObject(input0);
                    }
                    case 10022:{
                        StringObject input0 = new StringObject(vm, "com.google.android.googlequicksearchbox");
                        return  new ArrayObject(input0);
                    }
                    case 10023:{
                        StringObject input0 = new StringObject(vm, "com.android.connectivity.metrics");
                        return  new ArrayObject(input0);
                    }
                    case 10024:{
                        StringObject input0 = new StringObject(vm, "com.customermobile.preload.vzw");
                        return  new ArrayObject(input0);
                    }
                    case 10025:{
                        StringObject input0 = new StringObject(vm, "com.android.backupconfirm");
                        return  new ArrayObject(input0);

                    }
                    case 10026:{
                        StringObject input0 = new StringObject(vm, "com.google.android.ext.services");
                        return  new ArrayObject(input0);
                    }
                    case 10027:{
                        StringObject input0 = new StringObject(vm, "com.android.externalstorage");
                        return  new ArrayObject(input0);
                    }
                    case 10028:{
                        StringObject input0 = new StringObject(vm, "com.google.android.gms.setup");
                        return  new ArrayObject(input0);
                    }
                    case 10029:{

                        StringObject input0 = new StringObject(vm, "com.google.android.configupdater");
                        return  new ArrayObject(input0);
                    }
                    case 10030:{
                        StringObject input0 = new StringObject(vm, "com.android.documentsui");
                        return  new ArrayObject(input0);
                    }

                    case 10031:{
                        StringObject input0 = new StringObject(vm, "com.verizon.mips.services");
                        return  new ArrayObject(input0);
                    }
                    case 10032:{
                        StringObject input0 = new StringObject(vm, "com.android.omadm.service");
                        return  new ArrayObject(input0);
                    }
                    case 10033:{
                        StringObject input0 = new StringObject(vm, "com.android.timezone.updater");
                        return  new ArrayObject(input0);
                    }
                    case 10034:{
                        StringObject input0 = new StringObject(vm, "com.google.android.packageinstaller");
                        return  new ArrayObject(input0);
                    }
                    case 10035:{
                        StringObject input0 = new StringObject(vm, "com.verizon.obdm");
                        return  new ArrayObject(input0);
                    }
                    case 10036:{
                        StringObject input0 = new StringObject(vm, "com.android.proxyhandler");

                        return  new ArrayObject(input0);
                    }
                    case 10037:{
                        StringObject input0 = new StringObject(vm, "com.android.hotwordenrollment.tgoogle");
                        return  new ArrayObject(input0);
                    }
                    case 10038:{
                        StringObject input0 = new StringObject(vm, "com.android.emergency");
                        return  new ArrayObject(input0);
                    }
                    case 10039:{
                        StringObject input0 = new StringObject(vm, "com.google.android.gms");
                        StringObject input1 = new StringObject(vm, "com.google.android.gsf");
                        StringObject input2 = new StringObject(vm, "com.google.android.backuptransport");

                        return  new ArrayObject(input0,input1,input2);
                    }
                    case 10040:{
                        StringObject input0 = new StringObject(vm, "com.android.hotwordenrollment.okgoogle");
                        return  new ArrayObject(input0);
                    }
                    case 10041:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.pixelmigrate");
                        return  new ArrayObject(input0);
                    }
                    case 10042:{
                        StringObject input0 = new StringObject(vm, "com.google.android.setupwizard");
                        return  new ArrayObject(input0);
                    }
                    case 10043:{
                        StringObject input0 = new StringObject(vm, "com.google.android.partnersetup");
                        return  new ArrayObject(input0);
                    }
                    case 10044:{
                        StringObject input0 = new StringObject(vm, "com.google.android.storagemanager");
                        return  new ArrayObject(input0);
                    }
                    case 10045:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.turbo");
                        return  new ArrayObject(input0);
                    }
                    case 10046:{
                        StringObject input0 = new StringObject(vm, "com.android.vending");
                        return  new ArrayObject(input0);
                    }
                    case 10047:{
                        StringObject input0 = new StringObject(vm, "com.google.android.onetimeinitializer");
                        return  new ArrayObject(input0);
                    }
                    case 10048:{
                        StringObject input0 = new StringObject(vm, "com.verizon.services");
                        return  new ArrayObject(input0);
                    }
                    case 10049:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.nexuslauncher");
                        return  new ArrayObject(input0);
                    }
                    case 10050:{
                        StringObject input0 = new StringObject(vm, "com.android.providers.calendar");
                        return  new ArrayObject(input0);
                    }
                    case 10051:{
                        StringObject input0 = new StringObject(vm, "com.android.cellbroadcastreceiver");
                        return  new ArrayObject(input0);
                    }
                    case 10052:{
                        StringObject input0 = new StringObject(vm, "com.android.hotwordenrollment.xgoogle");
                        return  new ArrayObject(input0);
                    }
                    case 10053:{
                        StringObject input0 = new StringObject(vm, "com.android.cts.priv.ctsshim");
                        return  new ArrayObject(input0);
                    }
                    case 10054:{
                        StringObject input0 = new StringObject(vm, "com.google.android.tetheringentitlement");
                        return  new ArrayObject(input0);
                    }
                    case 10055:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.work.oobconfig");
                        return  new ArrayObject(input0);
                    }
                    case 10056:{
                        StringObject input0 = new StringObject(vm, "com.android.defcontainer");
                        return  new ArrayObject(input0);
                    }
                    case 10057:{
                        StringObject input0 = new StringObject(vm, "com.google.android.timezone.data");
                        return  new ArrayObject(input0);
                    }
                    case 10058:{
                        StringObject input0 = new StringObject(vm, "com.android.vpndialogs");
                        return  new ArrayObject(input0);
                    }
                    case 10059:{
                        StringObject input0 = new StringObject(vm, "com.android.managedprovisioning");
                        return  new ArrayObject(input0);
                    }
                    case 10060:{
                        StringObject input0 = new StringObject(vm, "com.google.android.carrier.authdialog");
                        return  new ArrayObject(input0);
                    }
                    case 10061:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.gcs");
                        return  new ArrayObject(input0);
                    }
                    case 10062:{
                        StringObject input0 = new StringObject(vm, "com.android.cts.ctsshim");
                        return  new ArrayObject(input0);
                    }
                    case 10063:{
                        StringObject input0 = new StringObject(vm, "android.autoinstalls.config.google.nexus");
                        return  new ArrayObject(input0);
                    }
                    case 10064:{
                        StringObject input0 = new StringObject(vm, "com.android.providers.partnerbookmarks");
                        return  new ArrayObject(input0);
                    }
                    case 10065:{
                        StringObject input0 = new StringObject(vm, "com.google.android.tts");
                        return  new ArrayObject(input0);
                    }
                    case 10066:{
                        StringObject input0 = new StringObject(vm, "com.google.android.inputmethod.japanese");
                        return  new ArrayObject(input0);
                    }
                    case 10067:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.tycho");
                        return  new ArrayObject(input0);
                    }
                    case 10068:{
                        StringObject input0 = new StringObject(vm, "com.google.android.printservice.recommendation");
                        return  new ArrayObject(input0);
                    }
                    case 10069:{
                        StringObject input0 = new StringObject(vm, "com.google.android.soundpicker");
                        return  new ArrayObject(input0);
                    }
                    case 10070:{
                        StringObject input0 = new StringObject(vm, "com.google.android.calendar");
                        return  new ArrayObject(input0);

                    }
                    case 10071:{
                        StringObject input0 = new StringObject(vm, "com.android.bips");
                        return  new ArrayObject(input0);
                    }
                    case 10072:{
                        StringObject input0 = new StringObject(vm, "com.google.android.GoogleCamera");

                        return  new ArrayObject(input0);
                    }
                    case 10073:{
                        StringObject input0 = new StringObject(vm, "com.google.android.marvin.talkback");

                        return  new ArrayObject(input0);

                    }
                    case 10074:{
                        StringObject input0 = new StringObject(vm, "com.android.egg");

                        return  new ArrayObject(input0);

                    }
                    case 10075:{
                        StringObject input0 = new StringObject(vm, "com.android.carrierdefaultapp");

                        return  new ArrayObject(input0);

                    }
                    case 10076:{
                        StringObject input0 = new StringObject(vm, "com.google.android.videos");

                        return  new ArrayObject(input0);

                    }
                    case 10077:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.enterprise.dmagent");

                        return  new ArrayObject(input0);
                    }
                    case 10078:{
                        StringObject input0 = new StringObject(vm, "com.google.vr.apps.ornament");
                        return  new ArrayObject(input0);
                    }
                    case 10079:{
                        StringObject input0 = new StringObject(vm, "com.google.android.youtube");

                        return  new ArrayObject(input0);
                    }
                    case 10080:{
                        StringObject input0 = new StringObject(vm, "com.google.android.ext.shared");

                        return  new ArrayObject(input0);

                    }
                    case 10081:{
                        StringObject input0 = new StringObject(vm, "com.google.android.inputmethod.pinyin");

                        return  new ArrayObject(input0);

                    }
                    case 10082:{
                        StringObject input0 = new StringObject(vm, "com.google.android.inputmethod.latin");

                        return  new ArrayObject(input0);
                    }
                    case 10083:{
                        StringObject input0 = new StringObject(vm, "com.google.ar.core");

                        return  new ArrayObject(input0);
                    }
                    case 10084:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.docs");

                        return  new ArrayObject(input0);
                    }
                    case 10085:{
                        StringObject input0 = new StringObject(vm, "com.google.android.music");

                        return  new ArrayObject(input0);
                    }
                    case 10086:{
                        StringObject input0 = new StringObject(vm, "com.android.htmlviewer");
                        return  new ArrayObject(input0);
                    }
                    case 10087:{
                        StringObject input0 = new StringObject(vm, "com.vzw.apnlib");
                        return  new ArrayObject(input0);

                    }
                    case 10088:{
                        StringObject input0 = new StringObject(vm, "com.google.android.inputmethod.korean");
                        return  new ArrayObject(input0);
                    }
                    case 10089:{
                        StringObject input0 = new StringObject(vm, "com.android.safetyregulatoryinfo");
                        return  new ArrayObject(input0);

                    }
                    case 10090:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.wallpaper.nexus");
                        return  new ArrayObject(input0);

                    }
                    case 10091:{
                        StringObject input0 = new StringObject(vm, "com.ustwo.lwp");
                        return  new ArrayObject(input0);

                    }
                    case 10092:{
                        StringObject input0 = new StringObject(vm, "com.android.printspooler");
                        return  new ArrayObject(input0);

                    }
                    case 10093:{
                        StringObject input0 = new StringObject(vm, "com.google.vr.vrcore");
                        return  new ArrayObject(input0);

                    }
                    case 10094:{
                        StringObject input0 = new StringObject(vm, "com.google.android.syncadapters.contacts");
                        return  new ArrayObject(input0);

                    }
                    case 10095:{
                        StringObject input0 = new StringObject(vm, "com.android.bluetoothmidiservice");
                        return  new ArrayObject(input0);

                    }
                    case 10096:{
                        StringObject input0 = new StringObject(vm, "com.android.pacprocessor");
                        return  new ArrayObject(input0);

                    }
                    case 10097:{
                        StringObject input0 = new StringObject(vm, "com.google.android.webview");
                        return  new ArrayObject(input0);

                    }
                    case 10098:{
                        StringObject input0 = new StringObject(vm, "com.android.bookmarkprovider");
                        return  new ArrayObject(input0);

                    }
                    case 10099:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.multidevice.client");
                        return  new ArrayObject(input0);

                    }
                    case 10100:{
                        StringObject input0 = new StringObject(vm, "com.breel.geswallpapers");
                        return  new ArrayObject(input0);

                    }
                    case 10101:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.messaging");
                        return  new ArrayObject(input0);

                    }
                    case 10102:{
                        StringObject input0 = new StringObject(vm, "com.android.wallpaper.livepicker");
                        return  new ArrayObject(input0);

                    }
                    case 10103:{
                        StringObject input0 = new StringObject(vm, "com.google.android.calculator");
                        return  new ArrayObject(input0);

                    }
                    case 10104:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.cloudprint");
                        return  new ArrayObject(input0);

                    }
                    case 10105:{
                        StringObject input0 = new StringObject(vm, "com.android.captiveportallogin");
                        return  new ArrayObject(input0);

                    }
                    case 10106:{
                        StringObject input0 = new StringObject(vm, "com.android.dreams.basic");
                        return  new ArrayObject(input0);

                    }
                    case 10107:{
                        StringObject input0 = new StringObject(vm, "com.android.companiondevicemanager");
                        return  new ArrayObject(input0);

                    }
                    case 10108:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.inputmethod.hindi");
                        return  new ArrayObject(input0);

                    }
                    case 10109:{
                        StringObject input0 = new StringObject(vm, "com.android.certinstaller");
                        return  new ArrayObject(input0);

                    }
                    case 10110:{
                        StringObject input0 = new StringObject(vm, "com.android.facelock");

                        return  new ArrayObject(input0);

                    }
                    case 10111:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.photos");

                        return  new ArrayObject(input0);

                    }
                    case 10112:{
                        StringObject input0 = new StringObject(vm, "com.google.android.talk");

                        return  new ArrayObject(input0);

                    }
                    case 10113:{
                        StringObject input0 = new StringObject(vm, "com.google.android.deskclock");
                        return  new ArrayObject(input0);

                    }
                    case 10114:{
                        StringObject input0 = new StringObject(vm, "com.google.android.apps.maps");

                        return  new ArrayObject(input0);

                    }
                    case 10115:{
                        StringObject input0 = new StringObject(vm, "com.android.chrome");

                        return  new ArrayObject(input0);

                    }
                    case 10116:{
                        StringObject input0 = new StringObject(vm, "com.google.android.gm");

                        return  new ArrayObject(input0);

                    }
                    case 10117:{
                        StringObject input0 = new StringObject(vm, "com.qualcomm.ltebc_vzw");

                        return  new ArrayObject(input0);

                    }
                    case 10118:{
                        StringObject input0 = new StringObject(vm, "com.qualcomm.qti.telephonyservice");
                        return  new ArrayObject(input0);

                    }
                    case 10119:{
                        StringObject input0 = new StringObject(vm, "com.qualcomm.embms");

                        return  new ArrayObject(input0);

                    }
                    case 10120:{
                        StringObject input0 = new StringObject(vm, "com.qualcomm.shutdownlistner");

                        return  new ArrayObject(input0);

                    }
                    case 10121:{
                        StringObject input0 = new StringObject(vm, "qualcomm.com.vzw_msdc_api");

                        return  new ArrayObject(input0);

                    }
                    case 10122:{
                        StringObject input0 = new StringObject(vm, "com.topjohnwu.magisk");
                        return  new ArrayObject(input0);

                    }
                    case 10123:{
                        StringObject input0 = new StringObject(vm, "world.letsgo.booster.android.pro");
                        return  new ArrayObject(input0);

                    }
                    case 10124:{
                        StringObject input0 = new StringObject(vm, "com.coolapk.market");
                        return  new ArrayObject(input0);

                    }
                    case 10125:{
                        StringObject input0 = new StringObject(vm, "com.cplotus.app");

                        return  new ArrayObject(input0);

                    }
                    case 10126:{
                        StringObject input0 = new StringObject(vm, "com.rair.adbwifi");

                        return  new ArrayObject(input0);

                    }
                    case 10127:{
                        StringObject input0 = new StringObject(vm, "com.caratlover");

                        return  new ArrayObject(input0);

                    }
                    case 10128:{
                        StringObject input0 = new StringObject(vm, "com.qxhc.shihuituan");

                        return  new ArrayObject(input0);

                    }
                    case 10129:{
                        StringObject input0 = new StringObject(vm, "com.tencent.mm");

                        return  new ArrayObject(input0);

                    }
                    case 10130:{
                        StringObject input0 = new StringObject(vm, "org.meowcat.edxposed.manager");
                        return  new ArrayObject(input0);

                    }
                    case 10131:{
                        StringObject input0 = new StringObject(vm, "com.alibaba.android.rimet");
                        return  new ArrayObject(input0);

                    }

                    case 10132:{
                        StringObject input0 = new StringObject(vm, "com.kxll.authlogin");
                        return  new ArrayObject(input0);

                    }
                    case 10133:{
                        StringObject input0 = new StringObject(vm, "com.example.androiddemo");
                        return  new ArrayObject(input0);

                    }
                    case 10134:{
                        StringObject input0 = new StringObject(vm, "com.example.meituanuuid");
                        return  new ArrayObject(input0);

                    }
                    case 10135:{
                        StringObject input0 = new StringObject(vm, "org.autojs.autojspro");
                        return  new ArrayObject(input0);

                    }
                    case 10136:{
                        StringObject input0 = new StringObject(vm, "com.bigsing.changer");
                        return  new ArrayObject(input0);

                    }
                    case 10137:{
                        StringObject input0 = new StringObject(vm, "com.eg.android.AlipayGphone");
                        return  new ArrayObject(input0);

                    }
                    case 10138:{
                        StringObject input0 = new StringObject(vm, "cn.missfresh.application");
                        return  new ArrayObject(input0);

                    }
                    case 10139:{
                        StringObject input0 = new StringObject(vm, "com.sdex.activityrunner");
                        return  new ArrayObject(input0);

                    }
                    case 10140:{
                        StringObject input0 = new StringObject(vm, "me.ele");
                        return  new ArrayObject(input0);

                    }
                    case 10141:{
                        StringObject input0 = new StringObject(vm, "com.roysue.base64");
                        return  new ArrayObject(input0);

                    }
                    case 10142:{
                        StringObject input0 = new StringObject(vm, "com.huawei.appmarket");
                        return  new ArrayObject(input0);

                    }
                    case 10145:{
                        StringObject input0 = new StringObject(vm, "com.meituan.retail.v.android");
                        return  new ArrayObject(input0);

                    }
                    case 10151:{
                        StringObject input0 = new StringObject(vm, "com.wujie.chengxin");
                        return  new ArrayObject(input0);

                    }
                    case 10160:{
                        StringObject input0 = new StringObject(vm, "cn.chuci.and.wkfenshen");
                        return  new ArrayObject(input0);

                    }
                    case 10162:{
                        StringObject input0 = new StringObject(vm, "com.guoshi.httpcanary");
                        return  new ArrayObject(input0);

                    }
                    case 10163:{
                        StringObject input0 = new StringObject(vm, "com.taobao.taobao");
                        return  new ArrayObject(input0);

                    }
//                    case 10165:{
//                        StringObject input0 = new StringObject(vm, "com.sankuai.meituan");
//                        return  new ArrayObject(input0);
//
//                    }
                }
//                StringObject input2_1 = new StringObject(vm, "");// 第一个参数是env
//                return  new ArrayObject(input2_1);
//                return  new ArrayObject();
                    return null;
            }
            case "android/content/pm/PackageManager->getApplicationInfo(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;":{

                return vm.resolveClass("android/content/pm/ApplicationInfo").newObject(vaList.getObjectArg(0).getValue());

            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/Integer->toString(I)Ljava/lang/String;":{
                int num = vaList.getIntArg(0);
                return new StringObject(vm, String.valueOf(num));
            }
            case "com/meituan/android/common/mtguard/NBridge->getPicName()Ljava/lang/String;":{
                return new StringObject(vm, "ms_com.sankuai.meituan");
            }
            case "com/meituan/android/common/mtguard/NBridge->getSecName()Ljava/lang/String;":{
                return new StringObject(vm, "ppd_com.sankuai.meituan.xbt");
            }
            case "com/meituan/android/common/mtguard/NBridge->getAppContext()Landroid/content/Context;":{
                return vm.resolveClass("android/content/Context").newObject(null);
            }
            case "com/meituan/android/common/mtguard/NBridge->getMtgVN()Ljava/lang/String;":{
                return new StringObject(vm, "4.4.7.3");
            }
            case "com/meituan/android/common/mtguard/NBridge->getDfpId()Ljava/lang/String;":{
                return new StringObject(vm, "8ae35960f6bb39a4ee0621f701838ab695cefe583e92787f93a1c789");
            }
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature,vaList);
    }

    @Override
    public int getIntField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature){
            case "android/content/pm/PackageInfo->versionCode:I":{
                return 1100090405;
            }
            case "android/content/pm/ApplicationInfo->flags:I":{
                String packageName =  dvmObject.getValue().toString();
                System.out.println("here");
                switch (packageName){
                    case "com.android.safetyregulatoryinfo":
                    case "com.android.printspooler":
                    case "com.google.android.storagemanager":
                    case "com.breel.geswallpapers":
                    case "com.android.facelock":
                    case "com.android.providers.downloads":
                    case "com.android.providers.media":
                    case "com.google.android.packageinstaller":
                    case "com.android.settings":
                    case "com.google.android.soundpicker":
                    case "com.android.companiondevicemanager": {
                        return 952647237;
                    }
                    case "com.google.android.apps.wallpaper":
                    case "com.google.android.deskclock": {
                        return 952876741;
                    }
                    case "com.android.providers.blockednumber":{
                        return 814267973;
                    }
                    case "com.google.android.apps.maps":{
                        return 953925317;
                    }
                    case "com.google.android.dialer":{
                        return 550223557;
                    }
                    case "com.android.phone":{
                        return 952647245;
                    }
                    case "com.android.providers.telephony":{
                        return 1015791109;
                    }
                    case "com.alibaba.android.rimet":
                    case "com.huawei.appmarket":
                    case "com.caratlover":
                    case "com.coolapk.market": {
                        return 953695812;
                    }
                    case "com.google.android.apps.multidevice.client":{
                        return 818429509;
                    }
                    case "com.android.managedprovisioning":{
                        return 952679941;
                    }
                    case "com.google.android.inputmethod.pinyin":{
                        return 814399173;
                    }
                    case "com.google.android.wfcactivation":
                    case "com.android.dreams.basic": {
                        return 948485701;
                    }
                    case "com.android.emergency":{
                        return 952680005;
                    }
                    case "com.google.android.calculator":{
                        return 1019854533;
                    }
                    case "world.letsgo.booster.android.pro":{
                        return 952680004;
                    }
                    case "com.android.bluetooth":{
                        return 818462277;
                    }
                    case "com.cplotus.app":{
                        return 953728580;
                    }
                    case "com.android.providers.contacts":{
                        return 814235205;
                    }
                    case "com.sankuai.meituan":{
                        return 949501508;
                    }
                    case "com.meituan.retail.v.android":{
                        return 949501508;
                    }
                    case "com.android.keychain":{
                        return 814235205;
                    }
                    case "com.android.bips":{
                        return 952680005;
                    }
                    case "com.google.android.calendar":{
                        return 818659013;
                    }
                    case "com.android.providers.calendar":{
                        return 814235205;
                    }
                    case "com.android.defcontainer":{
                        return 948452933;
                    }
                    case "com.example.meituanuuid":{
                        return 952680006;
                    }
                    case "com.android.omadm.service":{
                        return 814235205;
                    }
                    case "com.google.android.apps.work.oobconfig":{
                        return 948452869;
                    }
                    case "com.android.providers.settings":{
                        return 948485637;
                    }
                    case "com.qxhc.shihuituan":{
                        return 952680004;
                    }
                    case "com.android.timezone.updater":{
                        return 948452933;
                    }
                    case "com.ustwo.lwp":{
                         return 948452933;
                    }
                    case "com.bigsing.changer":{
                        return 952680004;
                    }
                    case "com.android.inputdevices":{
                        return 948485637;
                    }
                    case "com.google.android.apps.pixelmigrate":{
                        return 948452933;
                    }
                    case "com.android.server.telecom":{
                        return 818429509;
                    }
                    case "com.google.android.contacts":{
                        return 818659013;
                    }
                    case "com.android.externalstorage":{
                        return 948485701;
                    }
                    case "com.tencent.mm":{
                        return 949501508;
                    }
                    case "com.android.documentsui":{
                        return 952745541;
                    }
                    case "cn.chuci.and.wkfenshen":{
                        return 953695812;
                    }
                    case "com.android.systemui":{
                        return 952647181;
                    }
                    case "com.android.providers.downloads.ui":{
                        return 952680005;
                    }
                    case "com.google.android.apps.photos":{
                        return 953794245;
                    }
                    case "com.google.android.GoogleCamera":{
                        return 819707077;
                    }
                    case "com.android.cellbroadcastreceiver":{
                        return 952745541;
                    }
                    case "com.google.android.apps.messaging":{
                        return 684441285;
                    }
                    case "com.android.location.fused":{
                        return 948485701;
                    }
                    case "com.google.android.hiddenmenu":{
                        return 948452933;
                    }
                    case "com.google.android.feedback":{
                        return 948485701;
                    }
                    case "com.android.providers.userdictionary":{
                        return 814267909;
                    }
                    case "com.google.android.apps.cloudprint":{
                        return 952876613;
                    }
                    case "com.google.android.apps.docs":{
                        return 684441285;
                    }
                    case "com.android.carrierdefaultapp":{
                        return 948485701;
                    }
                    case "com.android.certinstaller":{
                        return 948452933;
                    }
                    case "com.eg.android.AlipayGphone":{
                        return 949501508;
                    }
                    case "com.sdex.activityrunner":{
                        return 952680004;
                    }
                    case "com.rair.adbwifi":{
                        return 952647236;
                    }
                    case "com.google.android.marvin.talkback":{
                        return 684441285;
                    }
                    case "android":{
                        return 952679945;
                    }
                    case "com.android.egg":{
                        return 948485701;
                    }
                    case "com.google.android.ext.services":{
                        return 948485701;
                    }
                    case "com.google.android.ext.shared":{
                        return 948452933;
                    }
                    case "com.google.android.webview":{
                        return -1198997947;
                    }
                    case "android.auto_generated_rro__":{
                        return 8928769;
                    }
                    case "com.example.androiddemo":{
                        return 952647236;
                    }
                    case "com.verizon.services":{
                        return 948485701;
                    }
                    case "com.google.android.asdiv":{
                        return 948485701;
                    }
                   case "com.kxll.authlogin":{
                        return 952680004;
                    }
                    case "org.autojs.autojspro":{
                        return 953695812;
                    }
                    case "com.roysue.base64":{
                        return 952680262;
                    }
                    case "com.android.bluetoothmidiservice":{
                        return 948485701;
                    }
                    case "com.android.bookmarkprovider":{
                        return 948485701;
                    }
                    case "com.android.calllogbackup":{
                        return 814333509;
                    }
                    case "com.android.captiveportallogin":{
                        return 948485701;
                    }
                    case "com.google.android.ims":{
                        return 680017605;
                    }
                    case "com.google.android.carrier":{
                        return 948452933;
                    }
                    case "com.google.android.carriersetup":{
                        return 948485701;
                    }
                    case "com.android.chrome":{
                        return -1463042363;
                    }
                    case "com.android.backupconfirm":{
                        return 948452869;
                    }
                    case "com.android.connectivity.metrics":{
                        return 948452933;
                    }
                    case "com.android.cts.ctsshim":{
                        return 948485697;
                    }
                    case "com.android.cts.priv.ctsshim":{
                        return -1198997951;
                    }
                    case "com.android.providers.partnerbookmarks":{
                        return 411614789;
                    }
                    case "com.android.sdm.plugins.dcmo":{
                        return 948485701;
                    }
                    case "com.android.sdm.plugins.diagmon":{
                        return 948485701;
                    }
                    case "com.android.service.ims":{
                        return 948485709;
                    }
                    case "com.android.sharedstoragebackup":{
                        return 948551173;
                    }
                    case "com.android.wallpaperbackup":{
                        return 1015594501;
                    }
                    case "com.google.android.gms.setup":{
                        return 948485701;
                    }
                    case "com.google.SSRestartDetector":{
                        return 948485709;
                    }
                    case "com.google.vr.apps.ornament":{
                        return 948485701;
                    }
                    case "com.qti.qualcomm.datastatusnotification":{
                        return 948485709;
                    }
                    case "com.qti.service.colorservice":{
                        return 948485701;
                    }
                    case "com.qualcomm.atfwd":{
                        return 948485701;
                    }
                    case "com.qualcomm.embms":{
                        return 948485701;
                    }
                    case "com.qualcomm.fastdormancy":{
                        return 948485701;
                    }
                    case "com.qualcomm.qcrilmsgtunnel":{
                        return 948485701;
                    }
                    case "com.qualcomm.qti.ims":{
                        return 948485701;
                    }
                    case "com.qualcomm.qti.radioconfiginterface":{
                        return 948485701;
                    }
                    case "com.qualcomm.qti.telephonyservice":{
                        return 948485709;
                    }
                    case "com.qualcomm.shutdownlistner":{
                        return 948485701;
                    }
                    case "com.qualcomm.timeservice":{
                        return 948485701;
                    }
                    case "com.quicinc.cne.CNEService":{
                        return 948485709;
                    }
                    case "com.verizon.llkagent":{
                        return 948452933;
                    }
                    case "com.verizon.obdm_permissions":{
                        return 8928769;
                    }
                    case "com.google.android.configupdater":{
                        return 948452933;
                    }
                    case "com.android.sdm.plugins.connmo":{
                        return 948485701;
                    }
                    case "com.verizon.obdm":{
                        return 952680005;
                    }
                    case "com.android.systemui.theme.dark":{
                        return 948485697;
                    }
                    case "android.autoinstalls.config.google.nexus":{
                        return 948452933;
                    }
                    case "com.google.android.apps.turbo":{
                        return 948485701;
                    }
                    case "com.google.android.apps.enterprise.dmagent":{
                        return 814235141;
                    }
                    case "org.meowcat.edxposed.manager":{
                        return 684211780;
                    }
                    case "com.google.android.inputmethod.latin":{
                        return 550158021;
                    }
                    case "com.google.android.gm":{
                        return 685489861;
                    }
                    case "com.google.android.googlequicksearchbox":{
                        return -1461993787;
                    }
                    case "com.google.android.backuptransport":{
                        return 948485701;
                    }
                    case "com.google.android.inputmethod.korean":{
                        return 814399045;
                    }
                    case "com.google.android.apps.gcs":{
                        return 952876613;
                    }
                    case "com.google.android.inputmethod.japanese":{
                        return 814399045;
                    }
                    case "com.google.android.timezone.data":{
                        return 948453061;
                    }
                    case "com.google.android.apps.inputmethod.hindi":{
                        return 948485701;
                    }
                    case "com.google.android.tts":{
                        return 550223557;
                    }
                    case "com.google.android.apps.helprtc":{
                        return 549994181;
                    }
                    case "com.google.android.onetimeinitializer":{
                        return 948485701;
                    }
                    case "com.google.android.videos":{
                        return 685489861;
                    }
                    case "com.google.android.gms":{
                        return -1194836283;
                    }
                    case "com.android.vending":{
                        return 684375749;
                    }
                    case "com.google.android.music":{
                        return 952876645;
                    }
                    case "com.google.ar.core":{
                        return -1463238971;
                    }
                    case "com.google.vr.vrcore":{
                        return -1194737979;
                    }
                    case "com.google.android.apps.wallpaper.nexus":{
                        return 948485697;
                    }
                    case "com.google.android.gsf":{
                        return 411582021;
                    }
                    case "com.google.android.partnersetup":{
                        return 948452933;
                    }
                    case "com.google.android.syncadapters.contacts":{
                        return 948452933;
                    }
                    case "com.google.android.talk":{
                        return 819576389;
                    }
                    case "com.android.htmlviewer":{
                        return 948485701;
                    }
                    case "com.guoshi.httpcanary":{
                        return 948452932;
                    }
                    case "com.android.statementservice":{
                        return 948452933;
                    }
                    case "com.android.wallpaper.livepicker":{
                        return 952680005;
                    }
                    case "com.topjohnwu.magisk":{
                        return 952647236;
                    }
                    case "com.android.mms.service":{
                        return 948485701;
                    }
                    case "com.android.mtp":{
                        return 948485701;
                    }
                    case "com.android.musicfx":{
                        return 948452933;
                    }
                    case "com.vzw.apnlib":{
                        return 948485701;
                    }
                    case "com.verizon.mips.services":{
                        return 953728581;
                    }
                    case "com.android.nfc":{
                        return 281591373;
                    }
                    case "com.htc.omadm.trigger":{
                        return 948452933;
                    }
                    case "com.android.hotwordenrollment.okgoogle":{
                        return 948485701;
                    }
                    case "org.codeaurora.ims":{
                        return 948485701;
                    }
                    case "com.android.pacprocessor":{
                        return 948485701;
                    }
                    case "com.google.android.theme.pixel":{
                        return 948485697;
                    }
                    case "com.google.android.setupwizard":{
                        return 952647173;
                    }
                    case "com.google.android.apps.nexuslauncher":{
                        return 1019985477;
                    }
                    case "com.android.service.ims.presence":{
                        return 948485701;
                    }
                    case "com.google.android.printservice.recommendation":{
                        return 948452869;
                    }
                    case "com.google.android.apps.tycho":{
                        return 814235205;
                    }
                    case "com.android.proxyhandler":{
                        return 948485701;
                    }
                    case "com.qualcomm.qti.rcsbootstraputil":{
                        return 948485709;
                    }
                    case "com.customermobile.preload.vzw":{
                        return 411614789;
                    }
                    case "com.qualcomm.qti.auth.secureextauthservice":{
                        return 948452933;
                    }
                    case "com.android.shell":{
                        return 948485701;
                    }
                    case "com.android.stk":{
                        return 948485701;
                    }
                    case "com.android.sdm.plugins.sprintdm":{
                        return 948452933;
                    }
                    case "com.android.hotwordenrollment.tgoogle":{
                        return 948485701;
                    }
                    case "com.google.android.tag":{
                        return 948485701;
                    }
                    case "com.google.android.tetheringentitlement":{
                        return 948485701;
                    }
                    case "com.google.android.carrier.authdialog":{
                        return 948452933;
                    }
                    case "com.android.vpndialogs":{
                        return 948452933;
                    }
                    case "com.android.vzwomatrigger":{
                        return 948452933;
                    }
                    case "qualcomm.com.vzw_msdc_api":{
                        return 952680005;
                    }
                    case "com.qualcomm.ltebc_vzw":{
                        return 948452933;
                    }
                    case "com.android.hotwordenrollment.xgoogle":{
                        return 948485701;
                    }
                    case "com.google.android.youtube":{
                        return 953925317;
                    }
                    default:
                        return 0;
                }

            }

        };
        return super.getIntField(vm, dvmObject, signature);
    }


    @Override
    public DvmObject<?> newObjectV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/Integer-><init>(I)V":
                int input = vaList.getIntArg(0);
                return DvmInteger.valueOf(vm, input);
        }
        return super.newObjectV(vm, dvmClass, signature, vaList);
    }

//    public static void main(String[] args) throws FileNotFoundException, DecoderException {
//
////        Logger.getLogger("com.github.unidbg.linux.ARM32SyscallHandler").setLevel(Level.DEBUG);
////        Logger.getLogger("com.github.unidbg.unix.UnixSyscallHandler").setLevel(Level.DEBUG);
////        Logger.getLogger("com.github.unidbg.AbstractEmulator").setLevel(Level.DEBUG);
////        Logger.getLogger("com.github.unidbg.linux.android.dvm.DalvikVM").setLevel(Level.DEBUG);
////        Logger.getLogger("com.github.unidbg.linux.android.dvm.BaseVM").setLevel(Level.DEBUG);
////        Logger.getLogger("com.github.unidbg.linux.android.dvm").setLevel(Level.DEBUG);
//
//        MtSign test = new MtSign();
//        System.out.println("****************");
//        // hook inline mmecpy
//
////        System.out.println(test.getHWProperty());
////        System.out.println(test.getHWStatus());
////        System.out.println(test.getPlatformInfo());
////        System.out.println(test.startCollection());
////        System.out.println(test.getEnvironmentInfo());
//        test.call111();
////        input:"0bd19fcc7fecca066ee71acb73e311aa9eaa5916fc1bf0c08b2398a5baf3a07f7eaa469ca481e46e4" +
////                "876c6e33f35348d4c05c0a683adb4d3b67f1ebdb1b0cd1244a4e60676be99f0686991c4713c27ee7e7f54210dafcb00e08c830" +
////                "af8846c1c"
////        3337303035623339353534643737643134666132653563636136336435343031403833326539343434386362326139323463633130633165363466313035663962306364346632353833653332653838343362366138373765
////        3337303035623339353534643737643134666132653563636136336435343031403833326539343434386362326139323463633130633165363466313035663962306364346632353833653332653838343362366138373765
////        test.call102("0bd19fcc7fecca066ee71acb73e311aa9eaa5916fc1bf0c08b2398a5baf3a07f7eaa469ca481e46e4" +
////                "876c6e33f35348d4c05c0a683adb4d3b67f1ebdb1b0cd1244a4e60676be99f0686991c4713c27ee7e7f54210dafcb00e08c830" +
////                "af8846c1c");
////
////        test.call102("265643e21d2c2a248880aef384dc0ab0cdf7c957b5bbbc5a1b0a8fe247abb7e0e474aa88331cfc9aab6356d26b5e" +
////                "ca7530579538986fbd8f6d6fa55350ce98e93d312361352ab5a15be859b94288f7ba8103c000d3d9f262ad35ad54f9b7d6e9523" +
////                "f48d84261a065fad58f901ef383e94a2e437800f6853dc510c69a130b26cdd4c19021ea516d26687ccd051f8ecdd1");
////
////        test.call102("2bb6a6010a9b92661636ea7d51ec0e3141fcc291088eb8766a09ac08e22938a0a0af67a08b44f544d9de4be1a876ff67");
//
//        // 失效的hookZz inline hook
////        test.hook_a7b96();
//        // unicorn hook yyds
////        test.hook_a7b96_unicorn();
//
//        // hook realloc
////        test.hookrealloc();
//
//        for(int i=0;i<20;i++){
//            SimpleDateFormat date = new SimpleDateFormat("yyyy年MM月dd日：HH:mm:ss---SSS(毫秒)");
//            String format = date.format(new Date(System.currentTimeMillis()));
//            System.out.println("——————————————————————————————————————————————————————"+format);
//
//            System.out.println(test.callMt());
//        }
//
//        // {"a0":"1.5","a1":"9b69f861-e054-4bc4-9daf-d36ae205ed3e","a2":"460e20150fd732693f7119f91da765f959e16f0f","a3":2,"a4":1623678446,"a5":"5trD1Tt/sDRLvnCxOlkNbPA7dPyFl73ANoieZrH2ywiFl5xzadVGa/zbxJM0JTOVO06EbQ+dEGEdDm71Ig0pPKCIMPnvmHdhjUhQcM5TetVHhyDR4cA2cyYUFyVwNgGAdWhv2aNkfZ+En+l2bWeleHWmClSaDYAjSM1QhHQVzaWGYoh8bbaHuIOeFXKfW4l5GHMoAvNWe9M2FcnQ/PEpMz24RTI1qc8jnPICJJKUvhqdcHf0OG6e5B90zScG8vVhS8+3aS2Nq4rXlCnaZzgWg9nd2TUc13ikImDCBw28MczUaimLagR0h1uxinc=","a6":0,"d1":"188bae0eb0d1b7b6ee12aa346855970fbdd3f696"}
//        // {"a0":"1.5","a1":"9b69f861-e054-4bc4-9daf-d36ae205ed3e","a2":"460e20150fd732693f7119f91da765f959e16f0f","a3":2,"a4":1616850562,"a5":"Knh8G/pYEiF5r46huJfG5fT5LeW8DvsT1Eln91K+5s3G0XJ4UZV9EHeQjIA48FgPXJCckSfXBceoHGb9d5+CE3HIlMmYOOZkiBUbncwD21FyniPeXLYkp4RXLnvgqb9tR2ivaVBbMqE+BnysbSV/qxhYMR3nSj6tAt4FTOeE1bJa029ms/3veNwUsALNPnE2H5SGlKGe60hxtXxJD8MMzEDpQLXvzId/fSr35rLPh1lTqJIaNqIYCrkEqNfcGI/VVuBS2Ue3GyQDfF4i9GT5OnDmb3MtrbxrVAhJEoS7xVdPWb+2GfPrwscSF9O5","a6":0,"d1":"292141a185752228091f56d99ff876513a90628d"}
//    }
    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags) {
        if (("/data/app/com.sankuai.meituan-TEfTAIBttUmUzuVbwRK1DQ==/base.apk").equals(pathname)) {
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/base.apk"), pathname));
        }
        if ("/proc/self/cmdline".equals(pathname)) {
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, "com.sankuai.meituan".getBytes()));
        }
        return null;
    }
    public static void main(String[] args) throws FileNotFoundException {
        MtSign mt =new MtSign();
        mt.call111();
        System.out.println("****************");

        System.out.println(mt.call501());
    }
    public static String bytesToHexString(byte[] bArr) {
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;

        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2){
                sb.append(0);
            }
            sb.append(sTmp.toUpperCase());
        }
        return sb.toString();
    }
}
