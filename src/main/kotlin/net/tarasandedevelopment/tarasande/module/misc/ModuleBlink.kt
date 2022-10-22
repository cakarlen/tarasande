package net.tarasandedevelopment.tarasande.module.misc

import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.network.ClientConnection
import net.minecraft.network.NetworkState
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientConnection
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.value.ValueBind
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import org.lwjgl.glfw.GLFW
import java.util.concurrent.CopyOnWriteArrayList

class ModuleBlink : Module("Blink", "Delays packets", ModuleCategory.MISC) {

    private val affectedPackets = ValueMode(this, "Affected packets", true, "Serverbound", "Clientbound")
    private val mode = object : ValueMode(this, "Mode", false, "State-dependent", "Pulse blink", "Latency") {
        override fun onChange() = onDisable()
    }
    private val pulseDelay = object : ValueNumber(this, "Pulse delay", 0.0, 500.0, 1000.0, 1.0) {
        override fun isEnabled() = mode.isSelected(1)
        override fun onChange() = onDisable()
    }
    private val latency = object : ValueNumber(this, "Latency", 0.0, 500.0, 1000.0, 1.0) {
        override fun isEnabled() = mode.isSelected(2)
        override fun onChange() = onDisable()
    }
    private val cancelKey = object : ValueBind(this, "Cancel key", Type.KEY, GLFW.GLFW_KEY_UNKNOWN) {
        override fun isEnabled() = mode.isSelected(0) && affectedPackets.isSelected(0)
    }

    private val packets = CopyOnWriteArrayList<Triple<Packet<*>, EventPacket.Type, Long>>()
    private val timeUtil = TimeUtil()

    private var pos: Vec3d? = null
    private var velocity: Vec3d? = null
    private var rotation: Rotation? = null

    override fun onEnable() {
        pos = mc.player?.pos
        velocity = mc.player?.velocity
        rotation = Rotation(mc.player ?: return)
    }

    init {
        registerEvent(EventPacket::class.java, 9999) { event ->
            if (event.cancelled) return@registerEvent
            if (event.packet != null) {
                if (mc.networkHandler?.connection == null || mc.networkHandler?.connection?.channel?.attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY)?.get() != NetworkState.PLAY ||
                    (event.type == EventPacket.Type.RECEIVE && event.packet is DisconnectS2CPacket) ||
                    (!mode.isSelected(1) && mc.currentScreen is DownloadingTerrainScreen)
                ) {
                    this.switchState()
                    return@registerEvent
                }
                if (mode.isSelected(1) && mc.currentScreen is DownloadingTerrainScreen) {
                    onDisable()
                    return@registerEvent
                }
                if (affectedPackets.isSelected(event.type.ordinal)) {
                    packets.add(Triple(event.packet, event.type,
                        if (mode.isSelected(2))
                            System.currentTimeMillis() + latency.value.toLong()
                        else
                            System.currentTimeMillis()
                    ))
                    event.cancelled = true
                }
            }
        }

        registerEvent(EventPollEvents::class.java, 9999) {
            when {
                mode.isSelected(1) -> {
                    if (timeUtil.hasReached(pulseDelay.value.toLong())) {
                        onDisable(true)
                        timeUtil.reset()
                    }
                }

                mode.isSelected(2) -> onDisable(false)
            }
        }

        registerEvent(EventTick::class.java, 9999) { event ->
            if (event.state == EventTick.State.PRE) {
                if (pos == null || velocity == null || rotation == null)
                    onEnable()

                if (cancelKey.isEnabled())
                    if (cancelKey.wasPressed() > 0) {
                        packets.removeIf { it.second == EventPacket.Type.SEND }
                        onDisable(all = true, cancelled = true)
                        enabled = false
                    }
            }
        }
    }

    override fun onDisable() {
        onDisable(true)
    }

    fun onDisable(all: Boolean, cancelled: Boolean = false) {
        if (mc.networkHandler?.connection?.isOpen == true) {
            val copy = ArrayList<Triple<Packet<*>, EventPacket.Type, Long>>()
            packets.removeIf {
                if (all || System.currentTimeMillis() >= it.third) {
                    copy.add(it)
                    true
                } else
                    false
            }
            for (triple in copy) {
                if (all || System.currentTimeMillis() >= triple.third) {
                    when (triple.second) {
                        EventPacket.Type.SEND -> (mc.networkHandler?.connection as IClientConnection).tarasande_forceSend(triple.first)
                        EventPacket.Type.RECEIVE ->
                            if (mc.networkHandler?.connection?.packetListener is ClientPlayPacketListener)
                                (triple.first as Packet<ClientPlayPacketListener>).apply(mc.networkHandler?.connection?.packetListener as ClientPlayPacketListener)
                    }
                }
            }
        } else
            packets.clear()
        if (cancelled) {
            mc.player?.setPosition(pos)
            mc.player?.velocity = velocity
            mc.player?.yaw = rotation?.yaw!!
            mc.player?.pitch = rotation?.pitch!!
        }
    }
}
