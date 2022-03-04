package su.mandora.tarasande.screen.accountmanager

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import su.mandora.tarasande.base.screen.accountmanager.account.Account
import su.mandora.tarasande.base.screen.accountmanager.account.ManagerAccount
import su.mandora.tarasande.base.screen.accountmanager.environment.ManagerEnvironment
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.screen.accountmanager.subscreens.ScreenBetterAccount
import su.mandora.tarasande.screen.accountmanager.subscreens.ScreenBetterProxy
import su.mandora.tarasande.util.connection.Proxy
import su.mandora.tarasande.util.render.screen.ScreenBetter
import java.util.concurrent.ThreadLocalRandom


class ScreenBetterAccountManager : ScreenBetter(null) {

    val accounts = ArrayList<Account>()
    var currentAccount: Account? = null

    var mainAccount = -1

    var loginThread: Thread? = null
    var status: String? = null

    private var accountList: AlwaysSelectedEntryListWidgetAccount? = null

    private var loginButton: ButtonWidget? = null
    private var removeButton: ButtonWidget? = null
    private var setMainButton: ButtonWidget? = null
    private var addButton: ButtonWidget? = null
    private var randomButton: ButtonWidget? = null

    var proxy: Proxy? = null

    val managerAccount = ManagerAccount()
    val managerEnvironment = ManagerEnvironment()

    override fun init() {
        addDrawableChild(AlwaysSelectedEntryListWidgetAccount(client, width, height, 16, height - 46).also { accountList = it })

        addDrawableChild(ButtonWidget(width / 2 - 203, height - 46 + 2, 100, 20, Text.of("Login")) { logIn(accountList?.selectedOrNull?.account!!) }.also { loginButton = it })
        addDrawableChild(ButtonWidget(width / 2 - 101, height - 46 + 2, 100, 20, Text.of("Remove")) {
            if (accounts.indexOf(accountList?.selectedOrNull?.account) == mainAccount) mainAccount = -1
            accounts.remove(accountList?.selectedOrNull?.account)
            accountList?.reload()
            accountList?.setSelected(null)
        }.also { removeButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 1, height - 46 + 2, 100, 20, Text.of("Direct Login")) {
            client?.setScreen(ScreenBetterAccount(this, "Direct Login") {
                logIn(it)
            })
        }.also { addButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 103, height - 46 + 2, 100, 20, Text.of("Set Main")) {
            val account = accountList?.selectedOrNull?.account!!
            if (account.session == null) {
                status = Formatting.RED.toString() + "Account hasn't been logged into yet"
            } else {
                val index = accounts.indexOf(account)
                if (mainAccount != index) {
                    mainAccount = index
                    status = Formatting.YELLOW.toString() + account.getDisplayName() + " is now the Main-Account"
                } else {
                    mainAccount = -1
                    status = Formatting.YELLOW.toString() + account.getDisplayName() + " is no longer a Main-Account"
                }
            }
        }.also { setMainButton = it })

        addDrawableChild(ButtonWidget(width / 2 - 203, height - 46 + 2 + 20 + 2, 100, 20, Text.of("Direct Login")) { client?.setScreen(ScreenBetterAccount(this, "Direct Login") { logIn(it) }) })
        addDrawableChild(ButtonWidget(width / 2 - 101, height - 46 + 2 + 20 + 2, 100, 20, Text.of("Random Account")) { logIn(accounts[ThreadLocalRandom.current().nextInt(accounts.size)]) }.also { randomButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 1, height - 46 + 2 + 20 + 2, 100, 20, Text.of("Add")) {
            client?.setScreen(ScreenBetterAccount(this, "Add Account") { account ->
                accounts.add(account)
                accountList?.reload()
            })
        }.also { addButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 103, height - 46 + 2 + 20 + 2, 100, 20, Text.of("Back")) {
            RenderSystem.recordRenderCall {
                close()
            }
        })

        addDrawableChild(ButtonWidget(2, height - 2 - 20, 100, 20, Text.of("Proxy")) {
            client?.setScreen(ScreenBetterProxy(this, proxy) { proxy ->
                this.proxy = proxy
            })
        })

        tick()
        super.init()
    }

    override fun tick() {
        loginButton?.active = accountList?.selectedOrNull != null
        removeButton?.active = accountList?.selectedOrNull != null
        setMainButton?.active = accountList?.selectedOrNull != null
        if (accountList?.selectedOrNull != null)
            setMainButton?.active = accountList?.selectedOrNull?.account?.isSuitableAsMain()!!
        randomButton?.active = accounts.isNotEmpty()
        super.tick()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        drawCenteredText(matrices, textRenderer, if (status == null) "Account Manager" else status, width / 2, 8 - textRenderer.fontHeight / 2, -1)
        if (proxy != null)
            textRenderer.drawWithShadow(matrices, "Proxy", 4f, height.toFloat() - 2 - 20 - textRenderer.fontHeight * 2 - 2, -1)
        textRenderer.drawWithShadow(matrices, if (proxy == null) "No Proxy" else proxy?.socketAddress?.address?.hostAddress!! + ":" + proxy?.socketAddress?.port!! + " (" + proxy?.ping!! + "ms)", 4f, height.toFloat() - 2 - 20 - textRenderer.fontHeight, -1)
    }

    override fun close() {
        status = null
        super.close()
    }

    inner class AlwaysSelectedEntryListWidgetAccount(mcIn: MinecraftClient?, widthIn: Int, heightIn: Int, topIn: Int, bottomIn: Int) : AlwaysSelectedEntryListWidget<AlwaysSelectedEntryListWidgetAccount.EntryAccount>(mcIn, widthIn, heightIn, topIn, bottomIn, MinecraftClient.getInstance().textRenderer.fontHeight * 2) {
        internal fun reload() {
            this.clearEntries()
            for (account in accounts) {
                this.addEntry(EntryAccount(account))
            }
        }

        init {
            reload()
        }

        inner class EntryAccount(var account: Account) : Entry<EntryAccount>() {
            private var lastClick: Long = 0

            override fun mouseClicked(x: Double, y: Double, button: Int): Boolean {
                if (button == 0) {
                    if (System.currentTimeMillis() - lastClick < 300) {
                        logIn(account)
                    }
                    setSelected(this)
                    lastClick = System.currentTimeMillis()
                }
                return super.mouseClicked(x, y, button)
            }

            override fun render(matrices: MatrixStack?, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
                matrices?.push()
                matrices?.translate((width / 2f).toDouble(), (y + textRenderer.fontHeight - textRenderer.fontHeight / 2f).toDouble(), 0.0)
                matrices?.scale(2.0f, 2.0f, 2.0f)
                matrices?.translate(-(width / 2f).toDouble(), (-(y + textRenderer.fontHeight - textRenderer.fontHeight / 2f)).toDouble(), 0.0)
                drawCenteredText(matrices, textRenderer, Text.of((if (mainAccount == accounts.indexOf(account)) Formatting.YELLOW.toString() else "") + account.getDisplayName()), width / 2, y + 2, -1)
                matrices?.pop()
            }

            override fun getNarration(): Text = Text.of(account.getDisplayName())
        }
    }

    fun logIn(account: Account) {
        if (loginThread != null && loginThread?.isAlive!!) {
            loginThread?.stop()
        }
        Thread(RunnableLogin(account)).also { loginThread = it }.start()
    }

    inner class RunnableLogin(var account: Account) : Runnable {
        override fun run() {
            status = Formatting.YELLOW.toString() + "Logging in..."
            val prevAccount = currentAccount
            try {
                currentAccount = account
                account.logIn()
                (MinecraftClient.getInstance() /* This can't be "client" because it is called from ClientMain means it's null at this point in time */ as IMinecraftClient).setSession(account.session)
                status = Formatting.GREEN.toString() + "Logged in as \"" + account.getDisplayName() + "\""
            } catch (e: Throwable) {
                e.printStackTrace()
                status = if (e.message?.isEmpty()!!) Formatting.RED.toString() + "Login failed!" else Formatting.RED.toString() + e.message
                currentAccount = prevAccount
            }
        }
    }
}