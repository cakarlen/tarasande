package su.mandora.tarasande.system.screen.panelsystem.impl.fixed

import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.feature.rotation.api.RotationUtil
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleESP
import su.mandora.tarasande.system.screen.panelsystem.api.PanelFixed
import su.mandora.tarasande.util.extension.kotlinruntime.ignoreAlpha
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.*

class PanelRadar : PanelFixed("Radar", 100.0, 100.0, true) {

    private val scale = ValueNumber(this, "Scale", 0.0, 1.0, 3.0, 0.1)

    private val entitySize = ValueNumber(this, "Entity Circle Size", 1.0, 2.0, 3.0, 0.1)

    private val selfColor = ValueColor(this, "Color of self", 0.6, 1.0, 1.0)

    private val moduleESP by lazy { ManagerModule.get(ModuleESP::class.java) }

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (mc.player == null)
            return

        val pos = mc.player?.getLerpedPos(delta)!!
        val panelLength = sqrt(panelWidth * panelWidth + panelHeight * panelHeight)
        for (entity in mc.world?.entities!!) {
            if (!moduleESP.shouldRender(entity))
                continue

            val otherPos = entity.getLerpedPos(delta)!!
            val dist = sqrt((otherPos.x - pos.x).pow(2.0) + (otherPos.z - pos.z).pow(2.0)) * scale.value

            if (dist > panelLength)
                continue

            val yawDelta = RotationUtil.getYaw(pos.x, pos.z, otherPos.x, otherPos.z) - MathHelper.wrapDegrees(mc.player?.yaw!!) + 180

            val x = -sin(yawDelta / 360.0 * PI * 2) * dist
            val y = cos(yawDelta / 360.0 * PI * 2) * dist

            if (entity == mc.player)
                RenderUtil.fillCircle(context.matrices, this.x + panelWidth / 2F + x, this.y + panelHeight / 2F + y, entitySize.value, getIntFromColor(selfColor.getColor().red, selfColor.getColor().green, selfColor.getColor().blue))
            else
                RenderUtil.fillCircle(context.matrices, this.x + panelWidth / 2F + x, this.y + panelHeight / 2F + y, entitySize.value, entity.teamColorValue.ignoreAlpha())
        }
    }

    fun getIntFromColor(Red: Int, Green: Int, Blue: Int): Int {
        var Red = Red
        var Green = Green
        var Blue = Blue
        Red = Red shl 16 and 0x00FF0000 //Shift red 16-bits and mask out other stuff
        Green = Green shl 8 and 0x0000FF00 //Shift Green 8-bits and mask out other stuff
        Blue = Blue and 0x000000FF //Mask out anything not blue.
        return -0x1000000 or Red or Green or Blue //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}