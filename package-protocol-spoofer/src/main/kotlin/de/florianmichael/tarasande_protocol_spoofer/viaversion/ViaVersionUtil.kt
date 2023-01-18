package de.florianmichael.tarasande_protocol_spoofer.viaversion

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.api.type.Type
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ServerboundPackets1_12
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2
import de.florianmichael.tarasande_protocol_hack.TarasandeProtocolHack
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.IForgeNetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.handler.Fml1NetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.handler.ModernFmlNetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.handler.ModernFmlState
import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.minecraft.network.ClientConnection

object ViaVersionUtil {

    fun builtForgeChannelMappings() {
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML|HS"] = "fml:hs"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML|MP"] = "fml:mp"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML"] = "minecraft:fml"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FORGE"] = "minecraft:forge"
    }

    fun createForgeHandler(connection: ClientConnection): IForgeNetClientHandler {
        if (ViaLoadingBase.getTargetVersion().isNewerThan(VersionListEnum.r1_18_2)) return ModernFmlNetClientHandler(ModernFmlState.FML_4, connection)
        if (ViaLoadingBase.getTargetVersion().isNewerThan(VersionListEnum.r1_17_1)) return ModernFmlNetClientHandler(ModernFmlState.FML_3, connection)
        if (ViaLoadingBase.getTargetVersion().isNewerThan(VersionListEnum.r1_12_2)) return ModernFmlNetClientHandler(ModernFmlState.FML_2, connection)

        return Fml1NetClientHandler(connection)
    }

    fun spoofTeslaClientCustomPayload(channel: String, value: ByteArray): Boolean {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_12_2)) {
            val customPayload = PacketWrapper.create(ServerboundPackets1_12.PLUGIN_MESSAGE, TarasandeProtocolHack.viaConnection)
            customPayload.write(Type.STRING, channel)
            customPayload.write(Type.REMAINING_BYTES, value)

            customPayload.sendToServerRaw()
            return true
        }
        return false
    }
}
