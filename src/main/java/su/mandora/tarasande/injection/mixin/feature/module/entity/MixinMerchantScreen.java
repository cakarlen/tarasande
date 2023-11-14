package su.mandora.tarasande.injection.mixin.feature.module.entity;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.misc.ModuleAutoTrade;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleSafeWalk;

@Mixin(MerchantScreen.class)
public abstract class MixinMerchantScreen extends HandledScreen<MerchantScreenHandler> implements ScreenHandlerProvider<MerchantScreenHandler> {
    public MixinMerchantScreen(MerchantScreenHandler container, PlayerInventory playerInventory, Text name)
    {
        super(container, playerInventory, name);
    }

    @Shadow private int selectedIndex;

    private final ModuleAutoTrade moduleAutoTrade = ManagerModule.INSTANCE.get(ModuleAutoTrade.class);

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);

        if (moduleAutoTrade.getEnabled().getValue()) {
            moduleAutoTrade.nextTick(this::fillTrade);
        }
    }

    private void fillTrade() {
        var handler = ((MerchantScreenHandler) super.handler);
        handler.switchTo(this.selectedIndex);
        this.client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(this.selectedIndex));
    }
}