package net.tarasandedevelopment.tarasande.event

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.command.CommandSource
import net.minecraft.item.Item
import su.mandora.event.Event
import java.util.*

class EventTick(val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventItemCooldown(val item: Item, var cooldown: Float) : Event(false)
class EventHandleBlockBreaking(var parameter: Boolean) : Event(false)
class EventUpdateTargetedEntity(val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventIsSaddled(var saddled: Boolean) : Event(false)
class EventInvalidGameMode(val uuid: UUID) : Event(false)
class EventScreenInput(var doneInput: Boolean) : Event(true)
class EventChangeScreen : Event {
    var dirty = false
    var newScreen: Screen?
        set(value) {
            field = value
            dirty = true
        }

    constructor(newScreen: Screen?) : super(true) {
        this.newScreen = newScreen
        dirty = false
    }
}

class EventChildren(val screen: Screen, val elements: ArrayList<Element>) : Event(false)
class EventShowsDeathScreen(var showsDeathScreen: Boolean) : Event(false)
class EventTimeTravel(var time: Long) : Event(false)
class EventShutdown : Event(false)
class EventInputSuggestions(val reader: StringReader) : Event(false) {
    var dispatcher: CommandDispatcher<CommandSource>? = null
    var commandSource: CommandSource? = null
}

class EventDoAttack : Event(true)