package com.kero.common;

import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.Jni;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.virtualmodule.android.AndroidModule;

import java.io.File;


public class BaseAndroidEmulator extends AbstractJni implements IOResolver{
    public final AndroidEmulator emulator;
    public final VM vm;

    public BaseAndroidEmulator(String processName, String APKPath, String SoPath, boolean logging){
        this.emulator = createARMEmulator(processName);
        Memory memory = MemoryProcess(emulator, 28);
        this.vm = VmProcess(emulator, APKPath, memory);
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

    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags){
        /*
            处理文件IO
         */
        System.out.println("Path: " + pathname);
        return null;
    }
}
