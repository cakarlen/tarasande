package su.mandora.tarasande.module.misc

import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.mixin.accessor.IClientPlayNetworkHandler
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueMode
import java.util.function.Consumer

class ModuleResourcePackSpoofer : Module("Resource pack spoofer", "Changes the response to resource pack packets", ModuleCategory.MISC) {

    private val ignoreInvalidProtocol = ValueBoolean(this, "Ignore invalid protocol", true)
    private val mode = ValueMode(this, "Mode", false, "Accept", "Decline")
    private val acceptMode = object : ValueMode(this, "Accept mode", false, "Successful loaded", "Fail download") {
        override fun isEnabled() = mode.isSelected(0)
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventPacket) {
            if (event.type == EventPacket.Type.RECEIVE && event.packet is ResourcePackSendS2CPacket) {
                if (ignoreInvalidProtocol.value) {
                    if ((mc.networkHandler as IClientPlayNetworkHandler).tarasande_invokeResolveUrl(event.packet.url) == null)
                        return@Consumer
                }

                when {
                    mode.isSelected(0) -> {
                        mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED))
                        when {
                            acceptMode.isSelected(0) -> mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED))
                            acceptMode.isSelected(1) -> mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD))
                        }
                    }

                    mode.isSelected(1) -> mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.DECLINED))
                }
            }
        }
    }

}