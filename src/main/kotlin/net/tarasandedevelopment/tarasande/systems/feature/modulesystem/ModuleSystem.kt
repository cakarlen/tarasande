package net.tarasandedevelopment.tarasande.systems.feature.modulesystem

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import su.mandora.event.Event
import su.mandora.event.EventDispatcher
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.systems.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.file.FileModules
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.chat.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.combat.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.exploit.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.ghost.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.misc.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.player.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.panel.element.PanelElementsCategory
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.panel.fixed.PanelArrayList
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.impl.fixed.PanelNotifications
import org.lwjgl.glfw.GLFW
import java.util.function.Consumer

class ManagerModule(panelSystem: ManagerPanel, fileSystem: ManagerFile) : Manager<Module>() {

    init {
        add(
            ModuleSprint(),
            ModuleESP(),
            ModuleKillAura(),
            ModuleVelocity(),
            ModuleTimer(),
            ModuleScaffoldWalk(),
            ModuleSafeWalk(),
            ModuleFullBright(),
            ModuleSpammer(),
            ModuleSpeed(),
            ModuleVehicleFlight(),
            ModuleNoSlowdown(),
            ModuleTeams(),
            ModuleInventoryMove(),
            ModuleBlink(),
            ModuleFlight(),
            ModuleTickBaseManipulation(),
            ModuleWTap(),
            ModuleTrajectories(),
            ModuleAntiBot(),
            ModuleNoFOV(),
            ModuleKeepSprint(),
            ModuleMurderMystery(),
            ModuleNoSwing(),
            ModuleColorCorrection(),
            ModuleWorldTime(),
            ModuleNuker(),
            ModuleMovementRecorder(),
            ModuleBedESP(),
            ModuleFog(),
            ModuleProjectileAimBot(),
            ModuleNoHunger(),
            ModuleNoRotate(),
            ModuleQuakeAura(),
            ModuleBlockBot(),
            ModuleChestStealer(),
            ModuleAutoTool(),
            ModuleFreeCam(),
            ModuleRain(),
            ModuleNoFall(),
            ModuleCriticals(),
            ModuleReach(),
            ModuleAutoClicker(),
            ModuleAimAssist(),
            ModuleNoFriends(),
            ModuleBlockESP(),
            ModuleParkour(),
            ModuleNameProtect(),
            ModuleNoCramming(),
            ModuleStep(),
            ModuleResourcePackSpoofer(),
            ModuleMidClick(),
            ModuleTargetStrafe(),
            ModuleSneak(),
            ModuleAirStuck(),
            ModuleTNTBlock(),
            ModuleNoWeb(),
            ModuleClickTP(),
            ModuleBacktrace(),
            ModuleAntiFall(),
            ModuleHitBox(),
            ModuleFastPlace(),
            ModuleFastUse(),
            ModuleAntiBindingCurse(),
            ModuleCommandBlockBypass(),
            ModuleAntiParticleHide(),
            ModuleNoChatContext(),
            ModuleNoMiningTrace(),
            ModuleBlockChangeTracker(),
            ModuleTrueSight(),
            ModulePhase(),
            ModuleInventoryCleaner(),
            ModulePrediction(),
            ModulePerfectHorseJump(),
            ModuleTridentBoost(),
            ModuleAutoRespawn(),
            ModulePortalScreen(),
            ModuleAutoFish(),
            ModuleJesus(),
            ModuleFastClimb(),
            ModuleAntiAFK(),
            ModuleEntityControl(),
            ModuleFastBreak(),
            ModuleCivBreak(),
            ModuleHealingBot(),
            ModuleVehicleSpeed(),
            ModuleIgnoreResourcePackHash(),
            ModuleNoJumpCooldown(),
            ModuleEveryItemOnArmor(),
            ModuleNoRender(),
            ModuleNoStatusEffect(),
            ModuleAllowAllCharacters()
        )

        EventDispatcher.apply {
            add(EventTick::class.java) {
                if (it.state == EventTick.State.POST) {
                    for (module in list)
                        for (i in 0 until module.bind.wasPressed())
                            module.switchState()
                }
            }
            add(EventPacket::class.java) {
                if(it.type == EventPacket.Type.RECEIVE && it.packet is PlayerRespawnS2CPacket)
                    for (module in list)
                        if (module.disableWhen.isSelected(0) && module.enabled)
                            module.enabled = false
            }
            add(EventDisconnect::class.java) {
                for (module in list)
                    if (module.disableWhen.isSelected(1) && module.enabled)
                        module.enabled = false
            }
        }

        panelSystem.add(PanelArrayList(this))
        this.list.distinctBy { it.category }.forEach {
            panelSystem.add(PanelElementsCategory(this, it.category))
        }
        fileSystem.add(FileModules(this))
    }

}

open class Module(val name: String, val description: String, val category: String) {
    private val eventListeners = HashSet<Triple<Class<Event>, Int, Consumer<Event>>>()

    var enabled = false
        set(value) {
            if (field != value) if (value) {
                onEnable()
                eventListeners.forEach { EventDispatcher.add(it.first, it.second, it.third) }
                PanelNotifications.notify("$name is now enabled")
            } else {
                eventListeners.forEach { EventDispatcher.rem(it.first, it.third) }
                onDisable()
                PanelNotifications.notify("$name is now disabled")
            }

            field = value
        }

    val visible = ValueBoolean(this, "Visible in ArrayList", true)
    val disableWhen = ValueMode(this, "Auto disable", true, "Death", "Disconnect")
    val bind = ValueBind(this, "Bind", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)

    val mc: MinecraftClient = MinecraftClient.getInstance()

    fun switchState() {
        enabled = !enabled
    }

    open fun onEnable() {}
    open fun onDisable() {}

    fun <T : Event> registerEvent(clazz: Class<T>, priority: Int = 1000, c: Consumer<T>) {
        @Suppress("UNCHECKED_CAST") // BYPASS GENERICS ONCE AGAIN $$$$$$$
        eventListeners.add(Triple(clazz as Class<Event>, priority, c as Consumer<Event>))
    }
}

object ModuleCategory {
    const val COMBAT = "Combat"
    const val MOVEMENT = "Movement"
    const val PLAYER = "Player"
    const val RENDER = "Render"
    const val MISC = "Misc"
    const val GHOST = "Ghost"
    const val EXPLOIT = "Exploit"
    const val CHAT = "Chat"
}