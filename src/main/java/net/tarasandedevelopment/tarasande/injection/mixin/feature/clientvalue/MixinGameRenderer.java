package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.client.render.GameRenderer;
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.DebugValues;
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.camera.Camera;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Redirect(method = "getBasicProjectionMatrix",at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;setPerspective(FFFF)Lorg/joml/Matrix4f;"))
    public Matrix4f overwriteAspectRatio(Matrix4f instance, float fovy, float aspect, float zNear, float zFar) {
        if(((Camera) DebugValues.INSTANCE.getCamera().getValuesOwner()).getForceAspectRatio().getValue())
            aspect = (float) ((Camera) DebugValues.INSTANCE.getCamera().getValuesOwner()).getAspectRatio().getValue();
        return instance.setPerspective(fovy, aspect, zNear, zFar);
    }

}