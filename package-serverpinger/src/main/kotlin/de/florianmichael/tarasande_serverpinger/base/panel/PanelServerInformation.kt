package de.florianmichael.tarasande_serverpinger.base.panel

import de.florianmichael.tarasande_serverpinger.injection.accessor.IMultiplayerServerListWidgetSubServerEntry
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.ServerEntry
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.option.ServerList
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.ManagerGrabber
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl.GrabberServerInformationOffset
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

var emptyServer = ServerInfo("", "", false).apply {
    ping = 0L
    label = ScreenTexts.EMPTY
    playerCountLabel = ScreenTexts.EMPTY
    online = false
}

fun ServerInfo.copy(): ServerInfo {
    return ServerInfo.fromNbt(toNbt())
}

open class PanelServerInformation(private val owner: Any, private val finish: (server: ServerInfo) -> Unit) : Panel("Server Information", 300.0, FontWrapper.fontHeight() /* I can't access titleBarHeight yet TODO */ + 32.0, background = true, scissor = false) {

    companion object {
        var tooltip: MutableList<Text>? = null

        val emulatedMultiplayerScreen = object : MultiplayerScreen(null) {
            override fun setMultiplayerScreenTooltip(tooltip: MutableList<Text>?) {
                PanelServerInformation.tooltip = tooltip
            }

            override fun getServerList(): ServerList {
                return object : ServerList(mc) {
                    override fun size(): Int {
                        return 0
                    }

                    override fun saveFile() {
                        // what do I look like?
                    }
                }
            }
        }
    }

    var server = emptyServer.copy()
        set(value) {
            field = value
            createEntry()
        }

    private val emulatedWidget = MultiplayerServerListWidget(null, null, 0, 0, 0, 0, 0)
    private var serverEntry: ServerEntry? = null

    override fun getValueOwner() = owner

    fun createEntry() {
        // Prevent memory leaks
        val iterator = emulatedMultiplayerScreen.serverListPinger.clientConnections.iterator()
        while (iterator.hasNext()) {
            iterator.next().disconnect(Text.empty())
            iterator.remove()
        }
        serverEntry = emulatedWidget.ServerEntry(emulatedMultiplayerScreen, server).apply {
            (this as IMultiplayerServerListWidgetSubServerEntry).tarasande_setCompletionConsumer(finish)
        }
    }

    private val offset = ManagerGrabber.getConstant(GrabberServerInformationOffset::class.java) as Int /* sick, minecraft simple shifts everything by 5 units */

    override fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val hovered = RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), x, y + titleBarHeight, panelWidth, panelHeight - titleBarHeight)
        serverEntry?.render(matrices, 0, y.toInt() + titleBarHeight, x.toInt(), (panelWidth + offset).toInt(), (panelHeight - titleBarHeight).toInt(), mouseX, mouseY, hovered, delta)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        if (tooltip != null) {
            mc.currentScreen?.renderTooltip(matrices, tooltip, mouseX, mouseY)
            tooltip = null
        }
    }
}
