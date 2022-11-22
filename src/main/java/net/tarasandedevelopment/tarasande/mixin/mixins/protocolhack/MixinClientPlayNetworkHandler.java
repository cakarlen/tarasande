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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.tarasandedevelopment.tarasande.util.protocol.RecipeInfo;
import net.tarasandedevelopment.tarasande.util.protocol.Recipes_1_12_2;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract void onEntityStatus(EntityStatusS2CPacket packet);

    @Shadow public abstract void onSynchronizeTags(SynchronizeTagsS2CPacket packet);

    @Shadow public abstract void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet);

    @Inject(method = "onPing", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
    private void onPing(PlayPingS2CPacket packet, CallbackInfo ci) {
        if (VersionList.isNewerOrEqualTo(ProtocolVersion.v1_17))
            return;

        final int inventoryId = (packet.getParameter() >> 16) & 0xFF; // Fix Via Bug from 1.16.5 (Window Confirmation -> PlayPing) Usage for MiningFast Detection
        ScreenHandler handler = null;

        if (client.player == null) return;

        if (inventoryId == 0) handler = client.player.playerScreenHandler;
        if (inventoryId == client.player.currentScreenHandler.syncId) handler = client.player.currentScreenHandler;

        if (handler == null) ci.cancel();
    }


    @Inject(method = {"onGameJoin", "onPlayerRespawn"}, at = @At("TAIL"))
    private void injectOnOnGameJoinOrRespawn(CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            onEntityStatus(new EntityStatusS2CPacket(player, (byte) 28));
        }
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onOnGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_12_2)) {
            onSynchronizeTags(new SynchronizeTagsS2CPacket(new HashMap<>()));

            List<Recipe<?>> recipes = new ArrayList<>();
            List<RecipeInfo<?>> recipeInfos = Recipes_1_12_2.getRecipes();
            for (int i = 0; i < recipeInfos.size(); i++) {
                recipes.add(recipeInfos.get(i).create(new Identifier(String.valueOf(i))));
            }
            onSynchronizeRecipes(new SynchronizeRecipesS2CPacket(recipes));
        }
    }
}
