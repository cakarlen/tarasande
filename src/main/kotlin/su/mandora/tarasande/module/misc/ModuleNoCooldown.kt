package su.mandora.tarasande.module.misc

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventItemCooldown
import java.util.function.Consumer

class ModuleNoCooldown : Module("No cooldown", "Removes any cooldown from items", ModuleCategory.MISC) {

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventItemCooldown) {
            event.cooldown = 0.0f
        }
    }
}