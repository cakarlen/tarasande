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

package de.florianmichael.clampclient.injection.mixin.protocolhack.entity;

import com.mojang.authlib.GameProfile;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.event.EventSkipIdlePacket;
import de.florianmichael.clampclient.injection.mixininterface.IClientPlayerEntity_Protocol;
import net.tarasandedevelopment.tarasande.protocolhack.fix.ArmorUpdater1_8_0;
import net.tarasandedevelopment.tarasande.protocolhack.platform.ProtocolHackValues;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.event.EventDispatcher;

@Mixin(value = ClientPlayerEntity.class, priority = 2000)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity_Protocol {

    @Shadow
    public Input input;
    @Shadow
    public float lastYaw;
    @Shadow
    public float lastPitch;
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    @Shadow
    public boolean autoJumpEnabled;
    @Shadow
    @Final
    protected MinecraftClient client;
    @Shadow
    private boolean lastOnGround;
    @Shadow
    private int ticksSinceLastPositionPacketSent;
    @Shadow
    private double lastX;
    @Shadow
    private double lastBaseY;
    @Shadow
    private double lastZ;
    @Unique
    private boolean protocolhack_areSwingCanceledThisTick = false;
    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Shadow
    protected abstract boolean isCamera();

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"))
    public boolean fixMovement(ClientPlayerEntity instance) {
        if (this.isCamera()) {
            boolean bl4;
            double d = this.getX() - this.lastX;
            double e = this.getY() - this.lastBaseY;
            double f = this.getZ() - this.lastZ;
            double g = this.getYaw() - this.lastYaw;
            double h = this.getPitch() - this.lastPitch;
            if (VersionList.isNewerTo(ProtocolVersion.v1_8)) {
                ++this.ticksSinceLastPositionPacketSent;
            }
            double n = 2.05E-4;
            if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
                n = 9.0E-4D;
            }
            boolean bl3 = MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(n) || this.ticksSinceLastPositionPacketSent >= 20;
            bl4 = g != 0.0 || h != 0.0;
            if (this.hasVehicle()) {
                Vec3d vec3d = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0, vec3d.z, this.getYaw(), this.getPitch(), this.onGround));
                bl3 = false;
            } else if (bl3 && bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.onGround));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(this.getX(), this.getY(), this.getZ(), this.onGround));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.onGround));
            } else if (this.lastOnGround != this.onGround || ProtocolHackValues.INSTANCE.getSendIdlePacket().getValue()) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(this.onGround));
            } else {
                EventDispatcher.INSTANCE.call(new EventSkipIdlePacket());
            }
            if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
                ++this.ticksSinceLastPositionPacketSent;
            }

            if (bl3) {
                this.lastX = this.getX();
                this.lastBaseY = this.getY();
                this.lastZ = this.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }
            if (bl4) {
                this.lastYaw = this.getYaw();
                this.lastPitch = this.getPitch();
            }
            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.client.options.getAutoJump().getValue();
        }
        return false;
    }

    @Inject(method = "swingHand", at = @At("HEAD"), cancellable = true)
    public void injectSwingHand(Hand hand, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8) && protocolhack_areSwingCanceledThisTick) {
            ci.cancel();
        }

        protocolhack_areSwingCanceledThisTick = false;
    }

    @Inject(
            method = "tickMovement()V",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;sneaking:Z", ordinal = 0)
    )
    private void injectTickMovement(CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_14_4)) {
            if (this.input.sneaking) {
                this.input.movementSideways = (float) ((double) this.input.movementSideways / 0.3D);
                this.input.movementForward = (float) ((double) this.input.movementForward / 0.3D);
            }
        }
    }

    @Redirect(method = "tickMovement",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSwimming()Z", ordinal = 0))
    public boolean redirectIsSneakingWhileSwimming(ClientPlayerEntity _this) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_14_1)) {
            return false;
        } else {
            return _this.isSwimming();
        }
    }

    @Inject(method = "isWalking", at = @At("HEAD"), cancellable = true)
    public void easierUnderwaterSprinting(CallbackInfoReturnable<Boolean> ci) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_14_1)) {
            ci.setReturnValue(input.movementForward >= 0.8);
        }
    }

    @Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z", ordinal = 0))
    private boolean disableSprintSneak(Input input) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_14_1)) {
            return input.movementForward >= 0.8F;
        }

        return input.hasForwardMovement();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    private boolean redirectTickMovement(ClientPlayerEntity self) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_12_2)) {
            return false; // Disable all water related movement
        }

        return self.isTouchingWater();
    }

    @Override
    public int getArmor() {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
            return ArmorUpdater1_8_0.INSTANCE.armor(); // Fixes Armor HUD
        }
        return super.getArmor();
    }

    @Override
    public void protocolhack_cancelSwingOnce() {
        protocolhack_areSwingCanceledThisTick = true;
    }
}