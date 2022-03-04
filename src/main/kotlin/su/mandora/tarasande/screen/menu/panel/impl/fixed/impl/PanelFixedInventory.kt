package su.mandora.tarasande.screen.menu.panel.impl.fixed.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import su.mandora.tarasande.mixin.accessor.IInGameHud
import su.mandora.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import kotlin.math.floor

class PanelFixedInventory(x: Double, y: Double) : PanelFixed("Inventory", x, y, 150.0, 66.0, false, true) {

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        var m = 1

        for (q in 9..35) {
            val r = (x + (q % 9) * (panelWidth / 9.0)).toInt()
            val s = (y + MinecraftClient.getInstance().textRenderer.fontHeight.toDouble() + 2.0 + (floor(q / 9.0) - 1) * ((panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight.toDouble()) / 3)).toInt()
            (MinecraftClient.getInstance().inGameHud as IInGameHud).invokeRenderHotbarItem(r, s, MinecraftClient.getInstance().tickDelta, MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.inventory?.main?.get(q) as ItemStack, m++)
        }
    }

    override fun isVisible(): Boolean {
        for (q in 9..35) {
            val itemStack = MinecraftClient.getInstance().player?.inventory?.main?.get(q)
            if (itemStack != null && !itemStack.isEmpty)
                return true
        }
        return false
    }

}