package net.tarasandedevelopment.tarasande.screen.menu.information

import com.google.common.collect.Iterables
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.screen.menu.information.Information
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class InformationEntities : Information("World", "Entities") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().world == null)
            return null
        return Iterables.size(MinecraftClient.getInstance().world?.entities!!).toString()
    }
}

class InformationWorldTime : Information("World", "World Time") {

    var lastUpdate: Pair<Long, Long>? = null

    init {
        TarasandeMain.get().managerEvent.add(Pair(1, Consumer<Event> { event ->
            if (event is EventPacket) {
                if (event.type == EventPacket.Type.RECEIVE && event.packet is WorldTimeUpdateS2CPacket) {
                    lastUpdate = Pair(event.packet.timeOfDay, event.packet.time)
                }
            }
        }))
    }

    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().world == null)
            return null
        if (lastUpdate == null)
            return null
        return lastUpdate?.first.toString() + "/" + lastUpdate?.second
    }
}

class InformationSpawnPoint : Information("World", "Spawn Point") {
    private val decimalPlacesX = ValueNumber(this, "Decimal places: x", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesY = ValueNumber(this, "Decimal places: y", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesZ = ValueNumber(this, "Decimal places: z", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String {
        val pos = MinecraftClient.getInstance().world!!.spawnPos

        return StringUtil.round(pos.x.toDouble(), this.decimalPlacesX.value.toInt()) + " " + StringUtil.round(pos.y.toDouble(), this.decimalPlacesY.value.toInt()) + " " + StringUtil.round(pos.z.toDouble(), this.decimalPlacesZ.value.toInt())
    }
}