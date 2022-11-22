/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.GameVersion;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.tarasandedevelopment.tarasande.protocolhack.fix.PackFormats;
import org.apache.commons.codec.digest.DigestUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class MixinClientBuiltinResourcePackProvider {

    @Unique
    private File protocolhack_trackedFile;

    @Redirect(method = "getDownloadHeaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/SharedConstants;getGameVersion()Lnet/minecraft/GameVersion;"))
    private static GameVersion editHeaders() {
        return PackFormats.INSTANCE.current();
    }

    @Inject(method = "getDownloadHeaders", at = @At("TAIL"))
    private static void removeHeaders(CallbackInfoReturnable<Map<String, String>> cir) {
        if (VersionList.isOlderTo(ProtocolVersion.v1_14))
            cir.getReturnValue().remove("X-Minecraft-Version-ID");
        if (VersionList.isOlderTo(ProtocolVersion.v1_13)) {
            cir.getReturnValue().remove("X-Minecraft-Pack-Format");
            cir.getReturnValue().remove("User-Agent");
        }
    }

    @Inject(method = "verifyFile", at = @At("HEAD"))
    public void keepFile(String expectedSha1, File file, CallbackInfoReturnable<Boolean> cir) {
        protocolhack_trackedFile = file;
    }

    @Redirect(method = "verifyFile", at = @At(value = "INVOKE", target = "Lcom/google/common/hash/HashCode;toString()Ljava/lang/String;", remap = false))
    public String revertHashAlgorithm(HashCode instance) {
        try {
            if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
                //noinspection UnstableApiUsage,deprecation
                return Hashing.sha1().hashBytes(Files.toByteArray(protocolhack_trackedFile)).toString();
            } else if (VersionList.isOlderTo(ProtocolVersion.v1_18)) {
                return DigestUtils.sha1Hex(new FileInputStream(protocolhack_trackedFile));
            }
        } catch (IOException ignored) {
        }
        return instance.toString();
    }

    @Redirect(method = "verifyFile", at = @At(value = "INVOKE", target = "Ljava/lang/String;toLowerCase(Ljava/util/Locale;)Ljava/lang/String;"))
    public String disableIgnoreCase(String instance, Locale locale) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
            return instance;
        }
        return instance.toLowerCase(locale);
    }
}
