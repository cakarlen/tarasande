package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.item.BlockItem
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleAutoTrade : Module("Auto Trade", "Enables quick trading with villagers", ModuleCategory.MISC) {
    private var nextTick: Runnable? = null

    init {
        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE) {
                nextTick?.run();
                nextTick = null;
            }
        }
    }

    fun inventoryHasSpace(): Boolean {
        return mc.player?.inventory?.emptySlot != 1
    }

    fun nextTick(runnable: Runnable) {
        nextTick = runnable
    }
}