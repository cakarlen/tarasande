package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory
import org.lwjgl.glfw.GLFW

class ModuleVehicleFlight : Module("Vehicle flight", "Makes you fly with vehicles (e.g. boat, horses)", ModuleCategory.MOVEMENT) {

    private val verticalSpeed = ValueNumber(this, "Vertical speed", 0.0, 0.1, 1.0, 0.1)
    private val downwardsBind = ValueBind(this, "Downwards bind", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)

    init {
        registerEvent(EventMovement::class.java) { event ->
            val vehicle = mc.player?.vehicle
            if (vehicle != null) {
                if (event.entity == vehicle) {
                    var sign = 0.0
                    if (mc.options.jumpKey.isPressed) sign += 1.0
                    if (downwardsBind.isPressed()) sign -= 1.0

                    event.velocity.y = verticalSpeed.value * sign
                }
            }
        }
    }
}