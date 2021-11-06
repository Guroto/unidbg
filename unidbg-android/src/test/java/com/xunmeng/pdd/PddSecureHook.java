package com.xunmeng.pdd;

import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.Arm64Hook;
import com.github.unidbg.arm.ArmHook;
import com.github.unidbg.arm.HookStatus;
import com.github.unidbg.arm.backend.Backend;
import com.github.unidbg.arm.backend.CodeHook;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.hook.HookListener;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.utils.Inspector;
import com.sun.jna.Pointer;
import unicorn.Unicorn;


public class PddSecureHook {
    private final Emulator<?> emulator;
    private final Module module;
    private Pointer result = null;

    public PddSecureHook(Emulator<?> emulator, Module module) {
        this.emulator = emulator;
        this.module = module;
    }

    public void hook(final long start, final long end, final int length){
        this.emulator.getBackend().hook_add_new(new CodeHook() {
            @Override
            public void hook(Backend backend, long address, int size, Object user) {
                if(address == (module.base + start)){
//                    System.out.println("[HOOK][ADDRESS]: " + (module.base));
                    RegisterContext ctx = emulator.getContext();
                    Pointer args_0 = ctx.getPointerArg(0);
                    Pointer args_1 = ctx.getPointerArg(1);
                    Pointer args_2 = ctx.getPointerArg(2);
                    Pointer args_3 = ctx.getPointerArg(3);
//                    System.out.println("[HOOK][ARGS-0]" + args_0.getInt(0));
                    System.out.println("[HOOK][ARGS-1] " + args_1.getString(0));
                    System.out.println("[HOOK][ARGS-2]" + args_2.getString(0));
//                    System.out.println("[HOOK][ARGS-3]" + args_3.getInt(0));
//                    result = ctx.getPointerArg(1);
                    result = args_0;

                }else if(address == (module.base + end)){
                    RegisterContext ctx = emulator.getContext();
                    System.out.println("result: " + result.getPointer(0).getString(0) + " called from " +  ctx.getLRPointer());
//                    Inspector.inspect(result.getByteArray(0, length),
//                            "[HOOK][" + Long.toHexString(start) + "][RESULT]");
                }
            }

            @Override
            public void onAttach(Unicorn.UnHook unHook) {

            }

            @Override
            public void detach() {

            }
        }, module.base + start, module.base + end, null);
    }
}
