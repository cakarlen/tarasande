package su.mandora.tarasande.injection.mixin.feature.module.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventInteractBlock;
import net.minecraft.util.hit.BlockHitResult;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {
    public MixinClientPlayerInteractionManager(ClientWorld world, GameProfile profile) {
        super();
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    public void interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        EventInteractBlock eventInteractBlock = new EventInteractBlock(player.getMainHandStack().isEmpty() ? Hand.OFF_HAND : hand, hitResult);
        EventDispatcher.INSTANCE.call(eventInteractBlock);
        if (eventInteractBlock.getCancelled()) cir.setReturnValue(ActionResult.FAIL);
    }
}
