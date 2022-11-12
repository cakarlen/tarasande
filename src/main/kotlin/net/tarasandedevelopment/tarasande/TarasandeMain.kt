package net.tarasandedevelopment.tarasande

import com.google.gson.GsonBuilder
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Session
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.feature.friends.Friends
import net.tarasandedevelopment.tarasande.feature.tagname.TagName
import net.tarasandedevelopment.tarasande.protocolhack.TarasandeProtocolHack
import net.tarasandedevelopment.tarasande.protocolhack.platform.ProtocolHackValues
import net.tarasandedevelopment.tarasande.systems.base.packagesystem.ManagerPackage
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.systems.feature.clickmethodsystem.ManagerClickMethod
import net.tarasandedevelopment.tarasande.systems.feature.espsystem.ManagerESP
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.ManagerAccount
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.azureapp.ManagerAzureApp
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.environment.ManagerEnvironment
import net.tarasandedevelopment.tarasande.systems.screen.blursystem.ManagerBlur
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.ManagerClientMenu
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.ElementMenuScreenAccountManager
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.ManagerGraph
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.util.connection.Proxy
import org.slf4j.LoggerFactory
import java.io.File

class TarasandeMain {

    val name = "tarasande" // "lowercase gang" ~kennytv

    //@formatter:off
    // Base
    internal    val packageSystem           by lazy { ManagerPackage() }
                val valueSystem             by lazy { ManagerValue() }
    // Screen
        // Account Manager
                val accountSystem           by lazy { ManagerAccount() }
                val azureAppSystem          by lazy { ManagerAzureApp() }
                val environmentSystem       by lazy { ManagerEnvironment() }
        // Base
                val blurSystem              by lazy { ManagerBlur() }
                val graphSystem             by lazy { ManagerGraph() }
                val informationSystem       by lazy { ManagerInformation() }
                val panelSystem             by lazy { ManagerPanel() }
    // Feature
                val clickSpeedSystem        by lazy { ManagerClickMethod() }
                val espSystem               by lazy { ManagerESP() }
                val moduleSystem            by lazy { ManagerModule() }
                val screenExtensionSystem   by lazy { ManagerScreenExtension() }

    // Implemented features
                val protocolHack            by lazy { TarasandeProtocolHack() }
                val clientValues            by lazy { ClientValues() }
                val friends                 by lazy { Friends() }
                val tagName                 by lazy { TagName() }
                val clientMenuSystem        by lazy { ManagerClientMenu() }
    //@formatter:on


    val logger = LoggerFactory.getLogger(name)!!

    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!

    val rootDirectory = File(System.getProperty("user.home") + File.separator + name)

    var proxy: Proxy? = null

    companion object {
        private val instance: TarasandeMain = TarasandeMain()

        fun get(): TarasandeMain {
            return instance
        }
    }

    fun onPreLoad() {
    }

    fun onLateLoad() {

        ProtocolHackValues.update(ProtocolVersion.getProtocol(protocolHack.version.value.toInt()))

        val accountManager = clientMenuSystem.get(ElementMenuScreenAccountManager::class.java).screenBetterSlotListAccountManager

        if (MinecraftClient.getInstance().session?.accountType == Session.AccountType.LEGACY && accountManager.mainAccount != null) {
            accountManager.logIn(accountManager.accounts[accountManager.mainAccount!!])

            while (accountManager.loginThread != null && accountManager.loginThread!!.isAlive)
                Thread.sleep(50L) // synchronize

            accountManager.status = ""
        }

        // We can't guarantee that qdbus exists, nor can we guarantee that we are even using kde plasma, just hope for the best ^^
        if (Util.getOperatingSystem() == Util.OperatingSystem.LINUX) {
            try {
                Runtime.getRuntime().exec("qdbus org.kde.KWin /Compositor suspend")
            } catch (ignored: Throwable) {
            }
        }
    }

    fun onUnload() {
        if (Util.getOperatingSystem() == Util.OperatingSystem.LINUX) {
            try {
                Runtime.getRuntime().exec("qdbus org.kde.KWin /Compositor resume")
            } catch (ignored: Throwable) {
            }
            //TODO
            //FileIO.saveAll()
        }
    }
}