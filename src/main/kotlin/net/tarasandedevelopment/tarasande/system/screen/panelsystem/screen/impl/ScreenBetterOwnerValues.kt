package net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetter
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import org.lwjgl.glfw.GLFW
import java.util.*

class ScreenBetterOwnerValues(parent: Screen, val titleName: String, val owner: Any) : ScreenBetter(parent) {

    private var clickableWidgetPanel: ClickableWidgetPanel? = null
    lateinit var panel: PanelElements<ElementWidthValueComponent>

    init {
        if (parent is ScreenCheatMenu)
            parent.disableAnimation = true
    }

    override fun init() {
        super.init()
        this.addDrawableChild(ClickableWidgetPanel(object : PanelElements<ElementWidthValueComponent>(this.titleName, 300.0, 0.0) {

            init {
                for (it in TarasandeMain.managerValue().getValues(owner))
                    elementList.add(it.createValueComponent())
            }

            override fun init() {
                super.init()

                this.x = (MinecraftClient.getInstance().window.scaledWidth / 2) - 150.0
            }

            override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
                this.panelHeight = getMaxScrollOffset() + titleBarHeight + 5.0 /* this is the padding for letting you scroll down a bit more than possible */
                val max = MinecraftClient.getInstance().window.scaledHeight
                if (this.panelHeight >= max)
                    this.panelHeight = max.toDouble()
                this.y = MinecraftClient.getInstance().window.scaledHeight / 2 - (this.panelHeight / 2)

                super.render(matrices, mouseX, mouseY, delta)
            }
        }.also { panel = it }).also { clickableWidgetPanel = it })
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        this.clickableWidgetPanel?.mouseReleased(mouseX, mouseY, button)
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        this.clickableWidgetPanel?.mouseScrolled(mouseX, mouseY, amount)
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        if (MinecraftClient.getInstance().world != null) {
            var prevScreen = prevScreen
            while (prevScreen is ScreenBetterOwnerValues)
                prevScreen = prevScreen.prevScreen
            prevScreen?.render(matrices, -1, -1, delta)
        }

        if (client?.world != null) {
            TarasandeMain.managerBlur().bind(setViewport = true, screens = true)
            RenderUtil.fill(matrices, 0.0, 0.0, client?.window?.scaledWidth?.toDouble()!!, client?.window?.scaledHeight?.toDouble()!!, -1)
            client?.framebuffer?.beginWrite(true)
        }

        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun hoveredElement(mouseX: Double, mouseY: Double): Optional<Element> {
        return Optional.of(clickableWidgetPanel ?: return Optional.empty())
    }

    override fun getFocused(): Element? {
        return clickableWidgetPanel
    }

    // Ultra 1337 hack because minecraft doesn't send escape key presses through to the widgets, means we have to ask our widgets here
    override fun close() {
        if (clickableWidgetPanel?.keyPressed(GLFW.GLFW_KEY_ESCAPE, 0, 0) == false)
            super.close()
    }

}