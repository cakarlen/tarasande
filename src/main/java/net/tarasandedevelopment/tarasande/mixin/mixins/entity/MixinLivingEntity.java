package net.tarasandedevelopment.tarasande.mixin.mixins.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventJump;
import net.tarasandedevelopment.tarasande.event.EventSwing;
import net.tarasandedevelopment.tarasande.mixin.accessor.ILivingEntity;
import net.tarasandedevelopment.tarasande.module.movement.ModuleFastClimb;
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation;
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 999 /* baritone fix */)
public abstract class MixinLivingEntity extends Entity implements ILivingEntity {

    @Shadow
    protected int bodyTrackingIncrements;

    @Shadow
    protected double serverYaw;

    @Shadow
    protected double serverPitch;

    @Shadow
    protected double serverX;

    @Shadow
    protected double serverY;

    @Shadow
    protected double serverZ;

    @Shadow
    protected double serverHeadYaw;
    @Shadow
    protected int lastAttackedTicks;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Shadow
    protected abstract void tickItemStackUsage(ItemStack stack);

    @Shadow
    private int jumpingCooldown;

    @Shadow
    public abstract boolean isClimbing();

    @Unique
    private float originalYaw;

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void injectPreJump(CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventJump eventJump = new EventJump(originalYaw = getYaw(), EventJump.State.PRE);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventJump);
            setYaw(eventJump.getYaw());
            if (eventJump.getCancelled())
                ci.cancel();
        }
    }

    @Inject(method = "jump", at = @At("TAIL"))
    public void injectPostJump(CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventJump eventJump = new EventJump(originalYaw, EventJump.State.POST);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventJump);
            setYaw(eventJump.getYaw());
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;bodyTrackingIncrements:I"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateTrackedPosition(DDD)V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setYaw(F)V")))
    public void injectTickMovement(CallbackInfo ci) {
        if (this.bodyTrackingIncrements > 0 && (Object) this == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null) {
            Rotation rotation = RotationUtil.INSTANCE.getFakeRotation();
            RotationUtil.INSTANCE.setFakeRotation(new Rotation(
                    (rotation.getYaw() + (float) MathHelper.wrapDegrees(this.serverYaw - (double) rotation.getYaw()) / (float) this.bodyTrackingIncrements) % 360.0F,
                    (rotation.getPitch() + (float) (this.serverPitch - (double) rotation.getPitch()) / (float) this.bodyTrackingIncrements) % 360.0F
            ));
        }
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"), cancellable = true)
    public void injectSwingHand(Hand hand, CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventSwing eventSwing = new EventSwing(hand);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventSwing);
            if (eventSwing.getCancelled())
                ci.cancel();
        }
    }

    @ModifyConstant(method = "applyMovementInput", constant = @Constant(doubleValue = 0.2))
    public double modifyClimbSpeed(double original) {
        if (!TarasandeMain.Companion.get().getDisabled() && isClimbing()) {
            ModuleFastClimb moduleFastClimb = TarasandeMain.Companion.get().getManagerModule().get(ModuleFastClimb.class);
            if (moduleFastClimb.getEnabled())
                original *= moduleFastClimb.getMultiplier().getValue();
        }
        return original;
    }

    @Override
    public double tarasande_getServerX() {
        return serverX;
    }

    @Override
    public double tarasande_getServerY() {
        return serverY;
    }

    @Override
    public double tarasande_getServerZ() {
        return serverZ;
    }

    @Override
    public double tarasande_getServerYaw() {
        return serverYaw;
    }

    @Override
    public double tarasande_getServerPitch() {
        return serverPitch;
    }

    @Override
    public int tarasande_getBodyTrackingIncrements() {
        return bodyTrackingIncrements;
    }

    @Override
    public void tarasande_setBodyTrackingIncrements(int bodyTrackingIncrements) {
        this.bodyTrackingIncrements = bodyTrackingIncrements;
    }

    @Override
    public int tarasande_getLastAttackedTicks() {
        return lastAttackedTicks;
    }

    @Override
    public void tarasande_setLastAttackedTicks(int lastAttackedTicks) {
        this.lastAttackedTicks = lastAttackedTicks;
    }

    @Override
    public void tarasande_invokeTickItemStackUsage(ItemStack itemStack) {
        this.tickItemStackUsage(itemStack);
    }

    @Override
    public int tarasande_getJumpingCooldown() {
        return jumpingCooldown;
    }

    @Override
    public void tarasande_setJumpingCooldown(int jumpingCooldown) {
        this.jumpingCooldown = jumpingCooldown;
    }
}