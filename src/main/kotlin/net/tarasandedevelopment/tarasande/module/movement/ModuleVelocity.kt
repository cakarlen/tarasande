package net.tarasandedevelopment.tarasande.module.movement

import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.event.EventVelocity
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.sqrt

class ModuleVelocity : Module("Velocity", "Reduces knockback", ModuleCategory.MOVEMENT) {

    private val packets = ValueMode(this, "Packets", true, "Velocity", "Explosion")
    private val mode = ValueMode(this, "Mode", false, "Cancel", "Custom", "Jump")
    private val horizontal = object : ValueNumber(this, "Horizontal", -1.0, 0.0, 1.0, 0.01) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val vertical = object : ValueNumber(this, "Vertical", 0.0, 0.0, 1.0, 0.01) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val delay = object : ValueNumber(this, "Delay", 0.0, 0.0, 20.0, 1.0) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val addition = object : ValueMode(this, "Addition", false, "Never", "Depending on packet", "Always") {
        override fun isEnabled() = delay.value > 0.0
    }
    private val changeDirection = object : ValueBoolean(this, "Change direction", false) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val chance = object : ValueNumber(this, "Chance", 0.0, 75.0, 100.0, 1.0) {
        override fun isEnabled() = mode.isSelected(2)
    }

    private var receivedKnockback = false
    private var lastVelocity: Vec3d? = null
    private var isJumping = false
    private var delays = ArrayList<Triple<Vec3d, Int, EventVelocity.Packet>>()

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventVelocity -> {
                if (!packets.isSelected(event.packet.ordinal)) return@Consumer

                when {
                    mode.isSelected(0) -> {
                        event.cancelled = true
                    }

                    mode.isSelected(1) -> {
                        if (delay.value > 0.0) {
                            delays.add(Triple(Vec3d(event.velocityX * horizontal.value, event.velocityY * vertical.value, event.velocityZ * horizontal.value), mc.player?.age!! + delay.value.toInt(), event.packet))
                        } else {
                            val newVelocity = if (changeDirection.value) Rotation(PlayerUtil.getMoveDirection().toFloat(), 0.0f).forwardVector(sqrt(event.velocityX * event.velocityX + event.velocityZ * event.velocityZ)) else Vec3d(event.velocityX, 0.0, event.velocityZ)
                            event.velocityX = newVelocity.x * horizontal.value
                            event.velocityY *= vertical.value
                            event.velocityZ = newVelocity.z * horizontal.value
                        }
                    }

                    else -> {
                        if (ThreadLocalRandom.current().nextInt(100) <= chance.value) {
                            lastVelocity = Vec3d(event.velocityX, event.velocityY, event.velocityZ)
                            receivedKnockback = true
                        }
                    }
                }
            }

            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    val iterator = delays.iterator()
                    while (iterator.hasNext()) {
                        val triple = iterator.next()
                        if (triple.second <= mc.player?.age!!) {
                            val newVelocity = if (changeDirection.value) Rotation(PlayerUtil.getMoveDirection().toFloat(), 0.0f).forwardVector(triple.first.horizontalLength()) else triple.first
                            mc.player?.velocity = if (addition.isSelected(2) || (addition.isSelected(1) && triple.third == EventVelocity.Packet.EXPLOSION)) mc.player?.velocity!! + newVelocity else newVelocity
                            iterator.remove()
                        }
                    }
                    when {
                        mode.isSelected(2) -> {
                            if (receivedKnockback) {
                                if (lastVelocity?.horizontalLengthSquared()!! > 0.01 && mc.player?.isOnGround!!)
                                    isJumping = true

                                receivedKnockback = false
                            }
                        }

                        else -> {
                            receivedKnockback = false
                        }
                    }
                    if (!mc.player?.isOnGround!!) {
                        isJumping = false
                    }
                }
            }

            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options.jumpKey)
                    event.pressed = event.pressed || isJumping
            }
        }
    }

}