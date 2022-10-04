package net.tarasandedevelopment.tarasande.screen.widget.panel

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.screen.menu.panel.Panel

open class ClickableWidgetPanel(val panel: Panel) : ClickableWidget(panel.x.toInt(), panel.y.toInt(), panel.panelWidth.toInt(), panel.panelWidth.toInt(), Text.of(panel.title)), Element {

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
    }

    init {
        panel.init()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (panel.isVisible())
            panel.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        panel.modifiable = true
        val returnType = panel.mouseClicked(mouseX, mouseY, button)
        panel.modifiable = false
        return panel.isVisible() && returnType
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        panel.mouseReleased(mouseX, mouseY, button)
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        return panel.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return panel.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        panel.charTyped(chr, modifiers)
        return false
    }

    fun tick() = panel.tick()

}