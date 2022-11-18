package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.base;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.viaprotocolhack.event.PipelineReorderEvent;
import de.florianmichael.viaprotocolhack.util.VersionList;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IClientConnection_Protocol;
import net.tarasandedevelopment.tarasande.protocolhack.fix.WolfHealthTracker1_14_4;
import net.tarasandedevelopment.tarasande.protocolhack.provider.vialegacy.FabricPreNettyProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.crypto.Cipher;

@Mixin(ClientConnection.class)
public class MixinClientConnection implements IClientConnection_Protocol {

    @Shadow
    public Channel channel;

    @Shadow
    private boolean encrypted;
    @Unique
    private UserConnection protocolhack_viaConnection;

    @Inject(method = "setCompressionThreshold", at = @At("RETURN"))
    private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
        channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
    }

    @Inject(method = "disconnect", at = @At("RETURN"))
    public void onDisconnect(Text disconnectReason, CallbackInfo ci) {
        WolfHealthTracker1_14_4.INSTANCE.clear();
    }

    @Inject(method = "setupEncryption", at = @At("HEAD"), cancellable = true)
    public void injectSetupEncryption(Cipher decryptionCipher, Cipher encryptionCipher, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            FabricPreNettyProvider.Companion.setDecryptionKey(decryptionCipher);
            FabricPreNettyProvider.Companion.setEncryptionKey(encryptionCipher);

            this.encrypted = true;
            ci.cancel();
        }
    }

    @Override
    public void protocolhack_setViaConnection(UserConnection userConnection) {
        this.protocolhack_viaConnection = userConnection;
    }

    @Override
    public UserConnection protocolhack_getViaConnection() {
        return this.protocolhack_viaConnection;
    }
}
