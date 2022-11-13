package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventTextVisit
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleNameProtect : Module("Name protect", "Hides your in-game name", ModuleCategory.RENDER) {

    private val protectedName = ValueText(this, "Protected name", TarasandeMain.instance.name)
    val hidePersonalName = ValueBoolean(this, "Hide personal name", true)

    private val border = "( |[^a-z]|\\b)"

    private fun replaceName(str: String, substring: String, replacement: String): String {
        val regex = Regex(border + substring + border)
        return regex.replace(str) {
            var newStr = ""

            val before = str[it.range.first]
            if (!isAlphabetical(before))
                newStr = before.toString()


            newStr += replacement

            val after = str[it.range.last]
            if (!isAlphabetical(after))
                newStr += after.toString()

            newStr
        }
    }

    private fun isAlphabetical(c: Char): Boolean = c in 'a'..'z' || c in 'A'..'Z' || c in '0' .. '9'

    init {
        registerEvent(EventTextVisit::class.java) { event ->
            event.string = replaceName(event.string, mc.session.profile.name, protectedName.value)

            for (pair in TarasandeMain.friends().friends) {
                event.string = replaceName(event.string, pair.first.name, pair.second ?: continue)
            }
        }
    }
}