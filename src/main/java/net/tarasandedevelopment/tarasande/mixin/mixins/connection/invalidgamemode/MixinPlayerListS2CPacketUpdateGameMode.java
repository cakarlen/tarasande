package net.tarasandedevelopment.tarasande.mixin.mixins.connection.invalidgamemode;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.GameMode;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventInvalidGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(targets = "net.minecraft.network.packet.s2c.play.PlayerListS2CPacket$Action$2")
public class MixinPlayerListS2CPacketUpdateGameMode {

    @Unique
    private UUID trackedUUID;

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readUuid()Ljava/util/UUID;"))
    public UUID trackUUID(PacketByteBuf instance) {
        this.trackedUUID = instance.readUuid();
        return this.trackedUUID;
    }

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;byId(I)Lnet/minecraft/world/GameMode;"))
    public GameMode fixGameMode(int id) {
        if (GameMode.byId(id, null) == null) {
            EventInvalidGameMode eventInvalidGameMode = new EventInvalidGameMode(trackedUUID);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventInvalidGameMode);
        }
        return GameMode.byId(id);
    }
}