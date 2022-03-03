package su.mandora.tarasande.screen.accountmanager.subscreens

import com.mojang.authlib.Environment
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.accountmanager.elements.TextFieldWidgetPlaceholder
import su.mandora.tarasande.util.render.screen.ScreenBetter
import java.util.function.Consumer

class ScreenBetterEnvironment(prevScreen: Screen?, private val environment: Environment?, private val environmentConsumer: Consumer<Environment>) : ScreenBetter(prevScreen) {

    private var authHostTextField: TextFieldWidget? = null
    private var accountsHostTextField: TextFieldWidget? = null
    private var sessionHostTextField: TextFieldWidget? = null
    private var servicesHostTextField: TextFieldWidget? = null

    override fun init() {
        authHostTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - 100, this.height / 2 - 50 - 15, 200, 20, Text.of("Auth Host")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = if (environment != null) environment.authHost else YggdrasilEnvironment.PROD.environment.authHost
            addDrawableChild(it)
        }

        accountsHostTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - 100, this.height / 2 - 25 - 15, 200, 20, Text.of("Accounts Host")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = if (environment != null) environment.accountsHost else YggdrasilEnvironment.PROD.environment.accountsHost
            addDrawableChild(it)
        }

        sessionHostTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - 100, this.height / 2 - 15, 200, 20, Text.of("Session Host")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = if (environment != null) environment.sessionHost else YggdrasilEnvironment.PROD.environment.sessionHost
            addDrawableChild(it)
        }

        servicesHostTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - 100, this.height / 2 + 25 - 15, 200, 20, Text.of("Services Host")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = if (environment != null) environment.servicesHost else YggdrasilEnvironment.PROD.environment.servicesHost
            addDrawableChild(it)
        }

        for ((index, environmentPreset) in TarasandeMain.get().screens?.betterScreenAccountManager?.managerEnvironment?.list?.withIndex()!!) {
            this.addDrawableChild(ButtonWidget(this.width - 100 * (index + 1), 0, 100, 20, Text.of(environmentPreset.name)) {
                authHostTextField?.text = environmentPreset.authHost
                accountsHostTextField?.text = environmentPreset.accountsHost
                sessionHostTextField?.text = environmentPreset.sessionHost
                servicesHostTextField?.text = environmentPreset.servicesHost
            })
        }

        this.addDrawableChild(ButtonWidget(width / 2 - 50, height / 2 + 50, 100, 20, Text.of("Done")) {
            environmentConsumer.accept(Environment.create(authHostTextField?.text!!, accountsHostTextField?.text!!, sessionHostTextField?.text!!, servicesHostTextField?.text!!, "Custom"))
            RenderSystem.recordRenderCall { close() }
        })

        this.addDrawableChild(ButtonWidget(width / 2 - 50, height / 2 + 50 + 25, 100, 20, Text.of("Abort")) {
            RenderSystem.recordRenderCall { close() }
        })

    }

}