package de.florianmichael.tarasande.protocolhack

import com.viaversion.viaversion.api.platform.providers.ViaProviders
import com.viaversion.viaversion.api.protocol.version.VersionProvider
import com.viaversion.viaversion.libs.gson.JsonArray
import com.viaversion.viaversion.libs.gson.JsonObject
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider
import de.florianmichael.tarasande.protocolhack.provider.FabricMovementTransmitterProvider
import de.florianmichael.tarasande.protocolhack.provider.FabricVersionProvider
import de.florianmichael.viaprotocolhack.INativeProvider
import de.florianmichael.viaprotocolhack.ViaProtocolHack
import de.florianmichael.viaprotocolhack.util.VersionList
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.metadata.Person
import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventDebugHud
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import java.io.File

class TarasandeProtocolHack : INativeProvider {

    private val version = ValueNumber(this, "Protocol", Double.MIN_VALUE, SharedConstants.getGameVersion().protocolVersion.toDouble(), Double.MAX_VALUE, 1.0, true)
    private val auto = ValueBoolean(this, "Auto", false)
    private val compression = arrayOf("decompress", "compress")

    init {
        ViaProtocolHack.instance().init(this)
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventDebugHud)
                event.list.add("[Protocol] " + VersionList.getProtocols().find { it.version == ViaProtocolHack.instance().current() }?.name)
        }
    }

    override fun isSinglePlayer(): Boolean {
        return MinecraftClient.getInstance().isInSingleplayer
    }

    fun setVersion(version: Int) {
        this.version.value = version.toDouble()
    }

    override fun clientsideVersion(): Int {
        return this.version.value.toInt()
    }

    override fun nettyOrder(): Array<String> {
        return this.compression
    }

    override fun run(): File {
        return File(System.getProperty("user.home") + File.separator + TarasandeMain.get().name)
    }

    override fun createDump(): JsonObject {
        val platformSpecific = JsonObject()
        val mods = JsonArray()

        FabricLoader.getInstance().allMods.stream().map { mod: ModContainer ->
            val jsonMod = JsonObject()
            jsonMod.addProperty("id", mod.metadata.id)
            jsonMod.addProperty("name", mod.metadata.name)
            jsonMod.addProperty("version", mod.metadata.version.friendlyString)
            val authors = JsonArray()
            mod.metadata.authors.stream().map { it: Person ->
                val info = JsonObject()
                val contact = JsonObject()
                it.contact.asMap().forEach { (property: String?, value: String?) -> contact.addProperty(property, value) }
                if (contact.size() != 0) {
                    info.add("contact", contact)
                }
                info.addProperty("name", it.name)
                info
            }.forEach { element: JsonObject? -> authors.add(element) }
            jsonMod.add("authors", authors)
            jsonMod
        }.forEach { element: JsonObject? -> mods.add(element) }

        platformSpecific.add("mods", mods)
        platformSpecific.addProperty("native version", SharedConstants.getGameVersion().protocolVersion)

        return platformSpecific
    }

    override fun createProviders(providers: ViaProviders?) {
        providers?.register(MovementTransmitterProvider::class.java, FabricMovementTransmitterProvider())
        providers?.register(VersionProvider::class.java, FabricVersionProvider())
    }

    fun isAuto() = this.auto.value

    fun toggleAuto() {
        this.auto.value = !this.auto.value
    }
}