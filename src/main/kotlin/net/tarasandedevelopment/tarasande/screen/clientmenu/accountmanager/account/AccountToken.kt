package net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.account

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.authlib.Environment
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.util.Session
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.AccountInfo
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.TextFieldInfo
import net.tarasandedevelopment.tarasande.screen.clientmenu.ElementMenuScreenAccountManager
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.environment.EnvironmentPresetEasyMC
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import java.util.*
import kotlin.math.acos


@AccountInfo(name = "Token")
class AccountToken : Account() {

    @TextFieldInfo("Redeem-Url", false, default = "https://api.easymc.io/v1/token/redeem")
    private var redeemUrl: String = ""

    @TextFieldInfo("Token", false)
    private var token: String = ""

    private var service: MinecraftSessionService? = null

    override fun defaultEnvironment() = TarasandeMain.get().managerClientMenu.get(ElementMenuScreenAccountManager::class.java).screenBetterAccountManager.managerEnvironment.get(EnvironmentPresetEasyMC::class.java).create()

    override fun logIn() {
        val http = URL(redeemUrl).openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.doOutput = true
        http.setRequestProperty("Content-Type", "application/json")
        http.connect()
        http.outputStream.write(JsonObject().also { it.addProperty("token", token) }.toString().toByteArray())

        val json = TarasandeMain.get().gson.fromJson(String(http.inputStream.readAllBytes()), JsonObject::class.java)

        val authenticationService = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment)

        session = Session(json.get("mcName").asString, json.get("uuid").asString, json.get("session").asString, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG)
        service = authenticationService.createMinecraftSessionService()
    }

    override fun getDisplayName() = if (session != null) session?.username!! else token

    override fun getSessionService() = this.service

    override fun save(): JsonArray {
        val jsonArray = JsonArray()
        jsonArray.add(redeemUrl)
        jsonArray.add(token)
        return jsonArray
    }

    override fun load(jsonArray: JsonArray): Account {
        val account = AccountToken()
        account.redeemUrl = jsonArray[0].asString
        account.token = jsonArray[1].asString

        return account
    }

    override fun create(credentials: List<String>) {
        redeemUrl = credentials[0]
        token = credentials[1]
    }
}