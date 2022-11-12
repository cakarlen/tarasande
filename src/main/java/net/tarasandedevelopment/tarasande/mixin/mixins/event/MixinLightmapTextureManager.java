package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.tarasandedevelopment.events.EventDispatcher;
import net.tarasandedevelopment.events.impl.EventGamma;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    public void hookEventGamma(NativeImage instance, int x, int y, int color) {
        EventGamma eventGamma = new EventGamma(x, y, color);
        EventDispatcher.INSTANCE.call(eventGamma);
        instance.setColor(x, y, eventGamma.getColor());
    }
}
