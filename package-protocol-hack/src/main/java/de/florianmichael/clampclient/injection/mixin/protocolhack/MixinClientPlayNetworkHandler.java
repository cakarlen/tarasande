/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract void onEntityStatus(EntityStatusS2CPacket packet);

    @Inject(method = "onPing", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
    private void onPing(PlayPingS2CPacket packet, CallbackInfo ci) {
        if (VersionList.isNewerOrEqualTo(ProtocolVersion.v1_17))
            return;

        final int inventoryId = (packet.getParameter() >> 16) & 0xFF; // Fix Via Bug from 1.16.5 (Window Confirmation -> PlayPing) Usage for MiningFast Detection
        ScreenHandler handler = null;

        if (client.player == null) return;

        if (inventoryId == 0) handler = client.player.playerScreenHandler;
        if (inventoryId == client.player.currentScreenHandler.syncId) handler = client.player.currentScreenHandler;

        if (handler == null) ci.cancel();
    }


    @Inject(method = { "onGameJoin", "onPlayerRespawn" }, at = @At("TAIL"))
    private void injectOnOnGameJoinOrRespawn(CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            onEntityStatus(new EntityStatusS2CPacket(player, (byte) 28));
        }
    }

    @Redirect(method = "onPlayerSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen;setReady()V"))
    public void moveDownloadingTerrainClosing(DownloadingTerrainScreen instance) {
        if (VersionList.isNewerOrEqualTo(ProtocolVersion.v1_19)) {
            instance.setReady();
        }
    }

    @Inject(method = "onPlayerPositionLook", at = @At("RETURN"))
    public void closeDownloadingTerrain(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_18_2) && MinecraftClient.getInstance().currentScreen instanceof DownloadingTerrainScreen) {
            MinecraftClient.getInstance().setScreen(null);
        }
    }

    @Inject(method = "onEntityPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateTrackedPositionAndAngles(DDDFFIZ)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void fixThreshold(EntityPositionS2CPacket packet, CallbackInfo ci, Entity entity) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_15_2)) {
            if (Math.abs(entity.getX() - packet.getX()) < 0.03125D && Math.abs(entity.getY() - packet.getY()) < 0.015625D && Math.abs(entity.getZ() - packet.getZ()) < 0.03125D) {
                ci.cancel();
                float g = (float)(packet.getYaw() * 360) / 256.0F;
                float h = (float)(packet.getPitch() * 360) / 256.0F;

                entity.updateTrackedPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), g, h, 3, true);
                entity.setOnGround(packet.isOnGround());
            }
        }
    }

    @Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getYaw()F", ordinal = 0))
    public float revertPrevYawSetter(Entity instance) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_18_2)) {
            if (this.client != null && this.client.player != null) {
                return this.client.player.prevYaw;
            }
        }
        return instance.getYaw();
    }

    @Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getYaw()F", ordinal = 1))
    public float revertYawSetter(Entity instance) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_18_2)) {
            if (this.client != null && this.client.player != null) {
                return this.client.player.getYaw();
            }
        }
        return instance.getYaw();
    }

    @Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getYaw()F", ordinal = 2))
    public float revertHeadYawSetter(Entity instance) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_18_2)) {
            if (this.client != null && this.client.player != null) {
                return this.client.player.getHeadYaw();
            }
        }
        return instance.getYaw();
    }
}
