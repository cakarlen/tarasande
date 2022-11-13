package net.tarasandedevelopment.tarasande.mixin.mixins.event.item;

import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import su.mandora.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.EventItemCooldown;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemCooldownManager.class)
public class MixinItemCooldownManager {

    @Inject(method = "getCooldownProgress", at = @At("RETURN"), cancellable = true)
    public void hookEventItemCooldown(Item item, float partialTicks, CallbackInfoReturnable<Float> cir) {
        EventItemCooldown eventItemCooldown = new EventItemCooldown(item, cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventItemCooldown);
        cir.setReturnValue(eventItemCooldown.getCooldown());
    }

}
