package net.tarasandedevelopment.tarasande_chat_features.module

import net.minecraft.client.MinecraftClient
import net.minecraft.network.encryption.PlayerKeyPair
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat.printChatMessage
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande_chat_features.CATEGORY_CHAT
import net.tarasandedevelopment.tarasande_chat_features.gatekeep.GatekeepTracker
import java.time.Instant
import java.util.function.Consumer

class ModulePublicKeyKicker : Module("Public key kicker", "Kicks players using outdated key signatures", CATEGORY_CHAT) {

    private var gatekeepTracker: GatekeepTracker? = null
    private var hasNotified = false

    init {
        gatekeepTracker = GatekeepTracker(MinecraftClient.getInstance().userApiService, MinecraftClient.getInstance().session.uuidOrNull, MinecraftClient.getInstance().runDirectory.toPath())
        gatekeepTracker?.getOldestValidKey()?.run {
            MinecraftClient.getInstance().profileKeys = this
        }

        registerEvent(EventDisconnect::class.java) {
            hasNotified = false
        }

        registerEvent(EventTick::class.java) {
            if (it.state != EventTick.State.PRE) return@registerEvent

            MinecraftClient.getInstance().profileKeys.fetchKeyPair().get().ifPresent(Consumer { key: PlayerKeyPair ->
                if (key.isExpired) {
                    if (!hasNotified) {
                        printChatMessage(Text.literal(
                                "Your public key has now expired! Anyone who joins after this message will be disconnected when you chat"
                        ))
                        hasNotified = true
                    }
                }
            })
        }

        TarasandeMain.managerInformation().add(object : Information(name, "Expiration time") {
            override fun getMessage(): String? {
                if (!enabled) return null

                val keyData = MinecraftClient.getInstance().profileKeys.fetchKeyPair().get()
                if (keyData.isPresent) {
                    return StringUtil.formatTime(keyData.get().refreshedAfter.toEpochMilli() - Instant.now().toEpochMilli())
                }
                return null
            }
        })
    }
}