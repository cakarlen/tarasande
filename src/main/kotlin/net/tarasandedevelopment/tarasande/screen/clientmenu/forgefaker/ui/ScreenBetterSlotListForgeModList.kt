package net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.ui

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.legacy.ModStruct
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.modern.ChannelStruct
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.modern.ModernForgePayload
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class ScreenBetterSlotListForgeModList(parent: Screen, private val titleName: String, val type: Type, val struct: IForgePayload) : ScreenBetterSlotList(46, 400, MinecraftClient.getInstance().textRenderer.fontHeight * 2 + 5) {

    enum class Type {
        MOD_LIST,
        CHANNEL_LIST
    }

    init {
        this.prevScreen = parent

        this.provideElements(object : ScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<ScreenBetterSlotListEntryForgeList> {
                return when (type) {
                    Type.MOD_LIST -> struct.installedMods().map { m -> ScreenBetterSlotListEntryForgeListMods(m) }
                    Type.CHANNEL_LIST -> (struct as ModernForgePayload).channels.map { m -> ScreenBetterSlotListEntryForgeListChannels(m) }
                }
            }
        })
    }

    override fun init() {
        super.init()

        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) {
            this.close()
        })
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        this.renderTitle(matrices, this.titleName)
    }

    abstract class ScreenBetterSlotListEntryForgeList : ScreenBetterSlotListEntry() {
        abstract fun display(): String

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            matrices.push()
            matrices.scale(2F, 2F, 2F)
            RenderUtil.textCenter(matrices, this.display(), entryWidth.toFloat() / 4F, 1F, -1)
            matrices.pop()
        }
    }

    class ScreenBetterSlotListEntryForgeListMods(private val modStruct: ModStruct) : ScreenBetterSlotListEntryForgeList() {
        override fun display() = this.modStruct.modId + " (" + this.modStruct.modVersion + ")"
    }

    class ScreenBetterSlotListEntryForgeListChannels(private val channelStruct: ChannelStruct) : ScreenBetterSlotListEntryForgeList() {
        override fun display() = this.channelStruct.name + " (" + this.channelStruct.version + ")"
    }
}