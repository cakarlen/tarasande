package net.tarasandedevelopment.tarasande.file

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.file.File
import kotlin.math.max
import kotlin.math.min

class FileCheatMenu : File("CheatMenu") {

    override fun save(): JsonElement {
        val jsonObject = JsonObject()
        for (panel in TarasandeMain.get().panelSystem.list) {
            val jsonArray2 = JsonArray()
            jsonArray2.add(panel.x)
            jsonArray2.add(panel.y)
            jsonArray2.add(panel.panelWidth)
            jsonArray2.add(panel.panelHeight)
            jsonArray2.add(panel.opened)
            jsonObject.add(panel.title, jsonArray2)
        }
        return jsonObject
    }

    override fun load(jsonElement: JsonElement) {
        val jsonObject = jsonElement.asJsonObject
        for (panel in TarasandeMain.get().panelSystem.list) {
            if (jsonObject.has(panel.title)) {
                val jsonArray2 = jsonObject.get(panel.title).asJsonArray
                panel.x = jsonArray2.get(0).asDouble
                panel.y = jsonArray2.get(1).asDouble

                panel.panelWidth = max(jsonArray2.get(2).asDouble, panel.minWidth)
                if (panel.maxWidth != null)
                    panel.panelWidth = min(panel.panelWidth, panel.maxWidth)

                panel.panelHeight = max(jsonArray2.get(3).asDouble, panel.minHeight)
                if (panel.maxHeight != null)
                    panel.panelHeight = min(panel.panelHeight, panel.maxHeight)

                panel.opened = jsonArray2.get(4).asBoolean
            }
        }
    }
}
