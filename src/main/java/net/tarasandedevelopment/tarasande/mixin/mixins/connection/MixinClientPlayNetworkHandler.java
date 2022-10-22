package net.tarasandedevelopment.tarasande.mixin.mixins.connection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventRespawn;
import net.tarasandedevelopment.tarasande.event.EventRotationSet;
import net.tarasandedevelopment.tarasande.event.EventVelocity;
import net.tarasandedevelopment.tarasande.module.exploit.ModuleNoChatContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "onEntityVelocityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocityClient(DDD)V"))
    public void hookedSetVelocityClient_onVelocityUpdate(Entity entity, double x, double y, double z) {
        EventVelocity eventVelocity = new EventVelocity(x, y, z, EventVelocity.Packet.VELOCITY);
        if (entity == client.player)
            TarasandeMain.Companion.get().getEventDispatcher().call(eventVelocity);
        if (!eventVelocity.getCancelled())
            entity.setVelocityClient(eventVelocity.getVelocityX(), eventVelocity.getVelocityY(), eventVelocity.getVelocityZ());
    }

    @Redirect(method = "onExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookedSetVelocityClient_onExplosion(Vec3d vec3d, double x, double y, double z) {
        EventVelocity eventVelocity = new EventVelocity(x, y, z, EventVelocity.Packet.EXPLOSION);
        TarasandeMain.Companion.get().getEventDispatcher().call(eventVelocity);
        return eventVelocity.getCancelled() ? vec3d : vec3d.add(eventVelocity.getVelocityX(), eventVelocity.getVelocityY(), eventVelocity.getVelocityZ());
    }

    @Inject(method = "onPlayerPositionLook", at = @At("TAIL"))
    public void injectOnPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        TarasandeMain.Companion.get().getEventDispatcher().call(new EventRotationSet(client.player.getYaw(), client.player.getPitch()));
    }

    @Redirect(method = "onDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;showsDeathScreen()Z"))
    public boolean injectShowsDeathScreen(ClientPlayerEntity instance) {
        EventRespawn eventRespawn = new EventRespawn(instance.showsDeathScreen());
        TarasandeMain.Companion.get().getEventDispatcher().call(eventRespawn);
        return eventRespawn.getShowDeathScreen();
    }

    @ModifyVariable(method = "acknowledge", at = @At("HEAD"), argsOnly = true, index = 2)
    public boolean modifyDisplayed(boolean value) {
        if (!TarasandeMain.Companion.get().getDisabled())
            if (TarasandeMain.Companion.get().getManagerModule().get(ModuleNoChatContext.class).getEnabled())
                return false;
        return value;
    }
}
