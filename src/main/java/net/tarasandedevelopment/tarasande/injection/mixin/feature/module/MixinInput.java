package net.tarasandedevelopment.tarasande.injection.mixin.feature.module;

import net.minecraft.client.input.Input;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleSprint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Input.class)
public class MixinInput {

    @Redirect(method = "hasForwardMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;movementForward:F"))
    public float hookSprint(Input instance) {
        ModuleSprint moduleSprint = TarasandeMain.Companion.managerModule().get(ModuleSprint.class);
        if (moduleSprint.getEnabled() && moduleSprint.getAllowBackwards().isEnabled() && moduleSprint.getAllowBackwards().getValue())
            return instance.getMovementInput().length();
        return instance.movementForward;
    }

}