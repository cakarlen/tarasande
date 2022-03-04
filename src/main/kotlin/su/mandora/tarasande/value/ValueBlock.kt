package su.mandora.tarasande.value

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.minecraft.block.Block
import net.minecraft.util.registry.Registry
import su.mandora.tarasande.base.value.Value
import java.util.concurrent.CopyOnWriteArrayList

open class ValueBlock(owner: Any, name: String, vararg blocks: Block) : Value(owner, name) {

    var list = CopyOnWriteArrayList<Block>()

    init {
        list.addAll(blocks)
    }

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        list.forEach { jsonArray.add(Registry.BLOCK.indexOf(it)) }
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray = jsonElement.asJsonArray
        list.clear()
        jsonArray.forEach { list.add(Registry.BLOCK.get(it.asInt)) }
    }

    open fun filter(block: Block) = true
}