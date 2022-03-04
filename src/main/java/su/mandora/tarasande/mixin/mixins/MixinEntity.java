package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventMovement;
import su.mandora.tarasande.event.EventVelocityYaw;
import su.mandora.tarasande.mixin.accessor.IEntity;
import su.mandora.tarasande.mixin.accessor.IVec3d;
import su.mandora.tarasande.util.math.rotation.RotationUtil;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Shadow
    protected abstract Vec3d getRotationVector(float pitch, float yaw);

    @Shadow
    protected static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Inject(method = "getRotationVec", at = @At("HEAD"), cancellable = true)
    public void injectGetRotationVec(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if ((Object) this == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null) {
            cir.setReturnValue(this.getRotationVector(RotationUtil.INSTANCE.getFakeRotation().getPitch(), RotationUtil.INSTANCE.getFakeRotation().getYaw()));
        }
    }

    // This method has a large oof momento, because you can't call the original method with modified args because static and modifying the method itself will make you unable to check whether the rotation of the ClientPlayerEntity or some random other piece of garbage is used
    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookedMovementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventVelocityYaw eventVelocityYaw = new EventVelocityYaw(yaw);
            TarasandeMain.Companion.get().getManagerEvent().call(eventVelocityYaw);
            yaw = eventVelocityYaw.getYaw();
        }
        return movementInputToVelocity(movementInput, speed, yaw);
    }

    @Inject(method = "move", at = @At("HEAD"))
    public void injectMove(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        EventMovement eventMovement = new EventMovement((Entity) (Object) this, movement);
        TarasandeMain.Companion.get().getManagerEvent().call(eventMovement);
        ((IVec3d) movement).copy(eventMovement.getVelocity());
    }

    @Override
    public Vec3d invokeGetRotationVector(float pitch, float yaw) {
        return getRotationVector(pitch, yaw);
    }
}