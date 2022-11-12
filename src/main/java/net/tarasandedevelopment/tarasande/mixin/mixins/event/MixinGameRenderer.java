package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.tarasandedevelopment.events.EventDispatcher;
import net.tarasandedevelopment.events.impl.EventRender2D;
import net.tarasandedevelopment.events.impl.EventUpdateTargetedEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;applyModelViewMatrix()V", shift = At.Shift.AFTER, remap = false))
    public void hookEventRender2D(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (client.player != null && !(MinecraftClient.getInstance().currentScreen instanceof DownloadingTerrainScreen)) {
            EventDispatcher.INSTANCE.call(new EventRender2D(new MatrixStack()));
        }
    }

    @Inject(method = "updateTargetedEntity", at = @At("HEAD"))
    public void hookEventUpdateTargetedEntityPre(float tickDelta, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.PRE));
    }

    @Inject(method = "updateTargetedEntity", at = @At("RETURN"))
    public void hookEventUpdateTargetedEntityPost(float tickDelta, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.POST));
    }
}
