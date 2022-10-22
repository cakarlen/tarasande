package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.signatures1_19_0;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IPublicKeyData_Protocol;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import java.util.UUID;

@Mixin(PlayerPublicKey.PublicKeyData.class)
public class MixinPlayerPublicKeySubPublicKeyData implements IPublicKeyData_Protocol {

    @Shadow @Final private Instant expiresAt;

    @Shadow @Final
    PublicKey key;

    @Unique
    private byte[] _1_19_0Key;

    @Redirect(method = { "write", "verifyKey" }, at = @At(value = "FIELD", target = "Lnet/minecraft/network/encryption/PlayerPublicKey$PublicKeyData;keySignature:[B"))
    public byte[] replaceKeys(PlayerPublicKey.PublicKeyData instance) {
        if (this._1_19_0Key != null && VersionList.isOlderOrEqualTo(VersionList.R1_19)) {
            return this._1_19_0Key;
        }

        return instance.keySignature();
    }

    @Inject(method = "toSerializedString", at = @At(value = "HEAD"), cancellable = true)
    public void injectToSerializedString(UUID playerUuid, CallbackInfoReturnable<byte[]> cir) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_19)) {
            cir.setReturnValue((this.expiresAt.toEpochMilli() + NetworkEncryptionUtils.encodeRsaPublicKey(this.key)).getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void tarasande_set1_19_0Key(ByteBuffer byteBuffer) {
        this._1_19_0Key = byteBuffer.array();
    }
}
