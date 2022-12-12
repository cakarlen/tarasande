package net.tarasandedevelopment.tarasande_litematica.panel

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande_litematica.generator.ManagerGenerator

class PanelLitematicaGenerators(generatorSystem: ManagerGenerator) : PanelElements<ElementWidthValueComponent>("Litematica Generators", 150.0, 100.0) {

    init {
        TarasandeMain.managerValue().getValues(generatorSystem).forEach {
            elementList.add(it.createValueComponent())
        }
    }
}