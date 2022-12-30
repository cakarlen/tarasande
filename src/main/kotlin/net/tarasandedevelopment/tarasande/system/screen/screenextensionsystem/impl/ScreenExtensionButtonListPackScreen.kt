package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.pack.PackScreen
import net.minecraft.resource.ZipResourcePack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import java.io.File
import java.util.logging.Level

class ScreenExtensionButtonListPackScreen : ScreenExtensionButtonList<PackScreen>(PackScreen::class.java) {

    init {
        add("Dump server pack", { MinecraftClient.getInstance().serverResourcePackProvider?.serverContainer != null }) {
            MinecraftClient.getInstance().serverResourcePackProvider?.serverContainer?.apply {
                // The pack provider, will always make ZipResourcePacks
                val base = (this.createResourcePack() as ZipResourcePack).backingZipFile

                val name = MinecraftClient.getInstance().currentServerEntry?.address ?: base.name

                var target = File(MinecraftClient.getInstance().resourcePackDir.toFile(), "$name.zip")
                var counter = 1
                while (target.exists()) {
                    target = File(MinecraftClient.getInstance().resourcePackDir.toFile(), "$name ($counter).zip")
                    counter++
                }
                try {
                    base.copyTo(target)
                } catch (t: Throwable) {
                    t.printStackTrace()
                    TarasandeMain.get().logger.log(Level.WARNING, "Wasn't able to copy $name to " + target.absolutePath)
                }
            }
        }

        add("Unload server pack", { MinecraftClient.getInstance().serverResourcePackProvider?.serverContainer != null }) {
            MinecraftClient.getInstance().serverResourcePackProvider.clear()
        }
    }
}
