package de.enzaxd.viaforge.injection.mixin.viaversion;

import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.MovementTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovementTracker.class)
public class MovementTrackerMixin {

    @Shadow private boolean ground;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void injectConstructor(CallbackInfo ci) {
        this.ground = false;
    }
}
