package de.florianmichael.tarasande_protocol_spoofer.spoofer

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanelToggleable
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.ManagerEntrySidebarPanel
import su.mandora.event.EventDispatcher
import java.util.UUID

class EntrySidebarPanelToggleableBungeeHack(sidebar: ManagerEntrySidebarPanel) : EntrySidebarPanelToggleable(sidebar, "Bungee Hack", "Spoofer") {

    private val endIP = ValueText(this, "End IP", "127.0.0.1")
    private val customUUID = ValueBoolean(this, "Custom UUID", false)
    private val uuid = object : ValueText(this, "UUID", UUID.randomUUID().toString()) {
        override fun isEnabled() = customUUID.value
    }

    private val zero = "\u0000"
    private fun stripID(input: String) = input.replace("-", "")

    init {
        EventDispatcher.add(EventPacket::class.java) { event ->
            if (event.type != EventPacket.Type.SEND) return@add
            if (event.packet !is HandshakeC2SPacket) return@add
            if (state.value) {
                var uuid = MinecraftClient.getInstance().session.uuid
                if (this.customUUID.value)
                    uuid = this.uuid.value

                (event.packet as HandshakeC2SPacket).address += this.zero + this.endIP.value + this.zero + this.stripID(uuid)
            }
        }
    }
}