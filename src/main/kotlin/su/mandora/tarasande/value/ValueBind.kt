package su.mandora.tarasande.value

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.event.EventKey
import su.mandora.tarasande.event.EventMouse

open class ValueBind(owner: Any, name: String, var type: Type, var button: Int, var mouse: Boolean = true) : Value(owner, name) {

    private var presses = 0
    private var mousePressed = false
    private var keyPressed = false

    init {
        TarasandeMain.get().managerEvent?.add { event ->
            when (event) {
                is EventMouse -> {
                    if (type == Type.MOUSE)
                        if (button == event.button) {
                            when (event.action) {
                                GLFW.GLFW_PRESS -> {
                                    mousePressed = true
                                    if (MinecraftClient.getInstance().currentScreen == null)
                                        presses++
                                }
                                GLFW.GLFW_RELEASE -> mousePressed = false
                            }
                        }
                }
                is EventKey -> {
                    if (type == Type.KEY)
                        if (event.key == button) {
                            when (event.action) {
                                GLFW.GLFW_PRESS -> {
                                    keyPressed = true
                                    if (MinecraftClient.getInstance().currentScreen == null)
                                        presses++
                                }
                                GLFW.GLFW_RELEASE -> keyPressed = false
                            }
                        }
                }
            }
        }
    }

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        jsonArray.add(type.ordinal)
        jsonArray.add(button)
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray = jsonElement.asJsonArray
        type = Type.values()[jsonArray.get(0).asInt]
        button = jsonArray.get(1).asInt
    }

    open fun filter(keyBind: Int): Boolean {
        return true
    }

    fun wasPressed(): Int {
        val prev = presses
        presses = 0
        return prev
    }

    fun isPressed(): Boolean {
        if (button == GLFW.GLFW_KEY_UNKNOWN) return false
        if (MinecraftClient.getInstance().currentScreen != null) return false

        return when (type) {
            Type.KEY -> {
                keyPressed
            }
            Type.MOUSE -> {
                mousePressed
            }
        }
    }

    enum class Type {
        KEY, MOUSE
    }
}