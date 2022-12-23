package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.BlockListChecker;
import net.minecraft.client.network.ServerAddress;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AllowedAddressResolver.class)
public class MixinAllowedAddressResolver {

    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/BlockListChecker;isAllowed(Lnet/minecraft/client/network/Address;)Z"))
    public boolean alwaysAllowAddressParsing(BlockListChecker instance, Address address) {
        if (TarasandeMain.Companion.clientValues().getAllowAddressParsingForBlacklistedServers().getValue()) {
            return true;
        }
        return instance.isAllowed(address);
    }

    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/BlockListChecker;isAllowed(Lnet/minecraft/client/network/ServerAddress;)Z"))
    public boolean alwaysAllowServerAddressParsing(BlockListChecker instance, ServerAddress serverAddress) {
        if (TarasandeMain.Companion.clientValues().getAllowAddressParsingForBlacklistedServers().getValue()) {
            return true;
        }
        return instance.isAllowed(serverAddress);
    }
}