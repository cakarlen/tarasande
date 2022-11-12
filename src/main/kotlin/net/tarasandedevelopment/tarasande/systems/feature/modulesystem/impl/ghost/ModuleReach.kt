package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.ghost

import net.tarasandedevelopment.tarasande.events.EventUpdateTargetedEntity
import net.tarasandedevelopment.tarasande.mixin.accessor.IGameRenderer
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleReach : Module("Reach", "Increases the hit reach", ModuleCategory.GHOST) {

    private val reach = ValueNumber(this, "Reach", 0.0, 3.0, 6.0, 0.1)

    private var origReach: Double? = null

    init {
        registerEvent(EventUpdateTargetedEntity::class.java) { event ->
            when (event.state) {
                EventUpdateTargetedEntity.State.PRE -> {
                    origReach = (mc.gameRenderer as IGameRenderer).tarasande_getReach()
                    (mc.gameRenderer as IGameRenderer).tarasande_setReach(reach.value)
                }

                EventUpdateTargetedEntity.State.POST -> {
                    (mc.gameRenderer as IGameRenderer).tarasande_setReach(origReach!!)
                }
            }
        }
    }
}
