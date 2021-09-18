package com.xunmeng.pdd;

import com.github.unidbg.Emulator;
import com.github.unidbg.arm.Arm64Hook;
import com.github.unidbg.arm.ArmHook;
import com.github.unidbg.arm.HookStatus;
import com.github.unidbg.hook.HookListener;
import com.github.unidbg.memory.SvcMemory;


public class PddSecureHook implements HookListener {
    private final Emulator<?> emulator;

    public PddSecureHook(Emulator<?> emulator) {
        this.emulator = emulator;
    }

    @Override
    public long hook(SvcMemory svcMemory, String libraryName, String symbolName, final long old) {
        if ("libpdd_secure.so".equals(libraryName)) {
            System.out.println("[symbolName] " + symbolName + " [old] " + old);
        }
        return 0;
    }
}
