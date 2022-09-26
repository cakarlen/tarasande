package de.enzaxd.viaforge.injection.mixin.viaversion;

import com.viaversion.viaversion.protocols.protocol1_9to1_8.ViaIdleThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ViaIdleThread.class)
public class ViaIdleThreadMixin {

    @Inject(method = "run", at = @At("HEAD"), cancellable = true, remap = false)
    public void injectRun(CallbackInfo ci) {
        ci.cancel();
    }
}