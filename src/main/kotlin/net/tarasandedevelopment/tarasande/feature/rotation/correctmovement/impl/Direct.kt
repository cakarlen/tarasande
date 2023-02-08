package net.tarasandedevelopment.tarasande.feature.rotation.correctmovement.impl

import net.tarasandedevelopment.tarasande.event.EventJump
import net.tarasandedevelopment.tarasande.event.EventVelocityYaw
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import su.mandora.event.EventDispatcher

class Direct(rotations: Rotations) {

    init {
        EventDispatcher.apply {
            add(EventJump::class.java) { event ->
                if (event.state != EventJump.State.PRE) return@add
                val fakeRotation = rotations.fakeRotation ?: return@add
                if (rotations.correctMovement.isSelected(2) || rotations.correctMovement.isSelected(3)) {
                    event.yaw = fakeRotation.yaw
                }
            }
            add(EventVelocityYaw::class.java) { event ->
                val fakeRotation = rotations.fakeRotation ?: return@add
                if (rotations.correctMovement.isSelected(2) || rotations.correctMovement.isSelected(3)) {
                    event.yaw = fakeRotation.yaw
                }
            }
        }
    }

}