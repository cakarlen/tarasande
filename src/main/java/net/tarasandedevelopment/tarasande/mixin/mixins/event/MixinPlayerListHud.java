package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventPlayerListName;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"))
    public Text hookEventPlayerListName(PlayerListHud instance, PlayerListEntry entry) {
        EventPlayerListName eventPlayerListName = new EventPlayerListName(entry, instance.getPlayerName(entry));
        TarasandeMain.Companion.get().getEventDispatcher().call(eventPlayerListName);
        return eventPlayerListName.getDisplayName();
    }

}