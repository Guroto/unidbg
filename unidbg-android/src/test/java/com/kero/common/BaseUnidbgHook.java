package com.kero.common;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.linux.android.SystemPropertyHook;
import com.github.unidbg.linux.android.SystemPropertyProvider;

public class BaseUnidbgHook {
    public static SystemPropertyHook systemPropertyHook(final AndroidEmulator emulator){
        SystemPropertyHook systemPropertyHook = new SystemPropertyHook(emulator);
        systemPropertyHook.setPropertyProvider(new SystemPropertyProvider() {
            @Override
            public String getProperty(String key) {
                System.out.println("[system_property_get]: " + key + " was called from " + emulator.<RegisterContext>getContext().getLRPointer());
                switch (key){
                    case "ro.product.brand":
                        return "BRAND";
                }
                return null;
            }
        });
        return systemPropertyHook;
    }

    public static void hookPopen(final AndroidEmulator emulator, int addr){
        emulator.attach().addBreakPoint(addr, new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                RegisterContext registerContext = emulator.getContext();
                String command = registerContext.getPointerArg(0).getString(0);
                System.out.println("[popen]: " + command);
                return true;
            }
        });
    }

    public static void hookTimeOfTody(final AndroidEmulator emulator, int addr){
        emulator.attach().addBreakPoint(addr, new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                return true;
            }
        });
    }
}
