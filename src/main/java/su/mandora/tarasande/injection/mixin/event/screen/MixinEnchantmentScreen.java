package su.mandora.tarasande.injection.mixin.event.screen;

import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.misc.ModuleAutoEnchant;
import su.mandora.tarasande.system.feature.modulesystem.impl.misc.ModuleAutoTrade;
import su.mandora.tarasande.util.player.PlayerUtil;
import su.mandora.tarasande.util.player.inventory.FindItemResult;

@Mixin(EnchantmentScreen.class)
public abstract class MixinEnchantmentScreen extends HandledScreen<EnchantmentScreenHandler> implements ScreenHandlerProvider<EnchantmentScreenHandler> {
    public MixinEnchantmentScreen(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    private final EnchantmentScreenHandler screenHandler = super.handler;
    private final Slot enchantSlot = handler.slots.get(0);
    private final Slot lapisSlot = handler.slots.get(1);
    private final ModuleAutoEnchant moduleAutoEnchant = ManagerModule.INSTANCE.get(ModuleAutoEnchant.class);
    private int enchantLevel = 0;

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);

        FindItemResult lapis = PlayerUtil.find(Items.LAPIS_LAZULI);
//        if (moduleAutoEnchant.getEnabled().getValue() && moduleAutoEnchant.fillLapis() && handler.getLapisCount() == 0)
//            InvUtils.shiftClick().from(lapis.slot()).toId(lapisSlot.id);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;

        for(int k = 0; k < 3; ++k) {
            enchantLevel = k;
            double d = mouseX - (double)(i + 60);
            double e = mouseY - (double)(j + 14 + 19 * k);
            if (d >= 0.0 && e >= 0.0 && d < 108.0 && e < 19.0 && ((EnchantmentScreenHandler)this.handler).onButtonClick(this.client.player, k)) {
                this.client.interactionManager.clickButton(((EnchantmentScreenHandler)this.handler).syncId, k);

//                if (moduleAutoEnchant.getEnabled().getValue())
//                    moduleAutoEnchant.nextTick(this::enchant);

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

//    private void enchant() {
//        moduleAutoEnchant.enchant(enchantSlot, lapisSlot, handler);
//    }
}
