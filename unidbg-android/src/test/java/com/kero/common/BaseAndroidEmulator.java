package com.kero.common;

import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.SystemPropertyHook;
import com.github.unidbg.linux.android.SystemPropertyProvider;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.Jni;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.linux.file.SimpleFileIO;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import com.kero.kit.FileProcess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public abstract class BaseAndroidEmulator extends AbstractJni implements IOResolver{
    public String processName;
    public String procDirPath;
    public final VM vm;
    public final int Pid;
    public final Module module;
    public DalvikModule dm;
    public final AndroidEmulator emulator;

    public BaseAndroidEmulator(String procName, String APKPath, String baseSoPath, String[] soList, boolean logging){
        processName = procName;
        procDirPath = "target/rootfs/proc/" + processName;
        emulator = createARMEmulator(processName);
        Pid = emulator.getPid();
        System.out.println("*[PID]: " + Pid);
        // 绑定IO重定向
        emulator.getSyscallHandler().addIOResolver(this);
        Memory memory = MemoryProcess(emulator, 23);
        // hook system property
        SystemPropertyHook systemPropertyHook = new SystemPropertyHook(emulator);
        systemPropertyHook.setPropertyProvider(new SystemPropertyProvider() {
            @Override
            public String getProperty(String key) {
                System.out.println("[system_property_get]: " + key);
                switch (key){
                    case "system_property_get":

                }
                return null;
            }
        });
        memory.addHookListener(systemPropertyHook);

        vm = VmProcess(emulator, APKPath, memory);
        // 调用JNI OnLoad
        vm.setJni(this);
        vm.setVerbose(logging);

        // 加载so文件至虚拟内容
        for (String s : soList) {
            dm = vm.loadLibrary(new File(baseSoPath + s), true);
            dm.callJNI_OnLoad(this.emulator);
        }

        module = dm.getModule();
    }

    private static VM VmProcess(AndroidEmulator emulator, String APKPath, Memory memory){
        VM vm = emulator.createDalvikVM(new File(APKPath));
        new AndroidModule(emulator, vm).register(memory);
        return vm;
    }

    public static Memory MemoryProcess(AndroidEmulator emulator, int SdkVersion){
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(SdkVersion));
        return memory;
    }

    private static AndroidEmulator createARMEmulator(String processName) {
        /*
            创建虚拟文件系统, 与IOResolver实现的具体文件处理并不冲突
        */
        return AndroidEmulatorBuilder.for32Bit()
                .setProcessName(processName)
                .addBackendFactory(new DynarmicFactory(true))
                .setRootDir(new File("target/rootfs"))
                .build();
    }

    public List<Object> initParams(int length){
        /*
            初始化参数列表, 默认参数1为JNIENV, 参数2为jobject
         */
        List<Object> params = new ArrayList<>(length);
        params.add(vm.getJNIEnv());
        params.add(0);
        return params;
    }

    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags){
        /*
            处理文件IO
         */
        System.out.println("*[Access Path]: " + pathname);
        if (("proc/" + emulator.getPid() + "/cmdline").equals(pathname)){
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, processName.getBytes()));
        }else if((("proc/" + emulator.getPid() + "/status").equals(pathname))){
            System.out.println(procDirPath + "/status");
            String content = FileProcess.readFile(procDirPath + "/status");
            content = content.replaceAll("__PID__", String.valueOf(this.Pid));
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, content.getBytes()));
        }
        return null;
    }
}
