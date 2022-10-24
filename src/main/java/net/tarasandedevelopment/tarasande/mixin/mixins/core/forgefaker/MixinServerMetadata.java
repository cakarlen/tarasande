package net.tarasandedevelopment.tarasande.mixin.mixins.core.forgefaker;

import net.minecraft.server.ServerMetadata;
import net.tarasandedevelopment.tarasande.mixin.accessor.forgefaker.IServerMetadata;
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerMetadata.class)
public class MixinServerMetadata implements IServerMetadata {

    @Unique
    private IForgePayload tarasande_forgePayload;

    @Override
    public IForgePayload getForgePayload() {
        return this.tarasande_forgePayload;
    }

    @Override
    public void setForgePayload(IForgePayload forgePayload) {
        this.tarasande_forgePayload = forgePayload;
    }
}