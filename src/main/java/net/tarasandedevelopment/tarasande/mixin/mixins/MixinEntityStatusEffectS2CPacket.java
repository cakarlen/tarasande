package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.render.ModuleAntiParticleHide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityStatusEffectS2CPacket.class)
public class MixinEntityStatusEffectS2CPacket {

    @Inject(method = "shouldShowIcon", at = @At("HEAD"), cancellable = true)
    public void injectShouldShowIcon(CallbackInfoReturnable<Boolean> cir) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleAntiParticleHide moduleAntiParticleHide = TarasandeMain.Companion.get().getManagerModule().get(ModuleAntiParticleHide.class);
            if(moduleAntiParticleHide.getEnabled() && moduleAntiParticleHide.getHud().getValue())
                cir.setReturnValue(true);
        }
    }

    @Inject(method = "shouldShowParticles", at = @At("HEAD"), cancellable = true)
    public void injectShouldShowParticles(CallbackInfoReturnable<Boolean> cir) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleAntiParticleHide moduleAntiParticleHide = TarasandeMain.Companion.get().getManagerModule().get(ModuleAntiParticleHide.class);
            if(moduleAntiParticleHide.getEnabled() && moduleAntiParticleHide.getInventory().getValue())
                cir.setReturnValue(true);
        }
    }
}
