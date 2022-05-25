package su.mandora.tarasande.module.render

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vector4f
import net.minecraft.util.registry.Registry
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.event.Priority
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventRender2D
import su.mandora.tarasande.event.EventRender3D
import su.mandora.tarasande.mixin.accessor.IMatrix4f
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueRegistry
import java.util.function.Consumer

class ModuleESP : Module("ESP", "Makes entities visible behind walls", ModuleCategory.RENDER) {

    val mode = ValueMode(this, "Mode", true, "Shader", "2D")
    private val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registry.ENTITY_TYPE, EntityType.PLAYER) {
        override fun keyToString(key: Any?) = (key as EntityType<*>).name.string
    }
//    private val espStudio = object : ValueButton(this, "ESP Studio") {
//        override fun onChange() {
//            mc.setScreen(null)
//        }
//    }

    fun filter(entity: Entity) = entities.list.contains(entity.type)

    private val hashMap = HashMap<Entity, Rectangle>()

    private fun project(modelView: Matrix4f, projection: Matrix4f, vector: Vec3d): Vec3d? {
        val camPos = mc.gameRenderer.camera.pos.negate().add(vector)
        val vec1 = matrixVectorMultiply(modelView, Vector4f(camPos.x.toFloat(), camPos.y.toFloat(), camPos.z.toFloat(), 1.0f))
        val screenPos = matrixVectorMultiply(projection, vec1)

        if (screenPos.w <= 0.0) return null

        val newW = 1.0 / screenPos.w * 0.5

        screenPos.set(
            (screenPos.x * newW + 0.5).toFloat(),
            (screenPos.y * newW + 0.5).toFloat(),
            (screenPos.z * newW + 0.5).toFloat(),
            newW.toFloat()
        )

        return Vec3d(
            screenPos.x * mc.window?.framebufferWidth!! / mc.window?.scaleFactor!!,
            (mc.window?.framebufferHeight!! - (screenPos.y * mc.window?.framebufferHeight!!)) / mc.window?.scaleFactor!!,
            screenPos.z.toDouble()
        )
    }

    private fun matrixVectorMultiply(matrix4f: Matrix4f, vector: Vector4f): Vector4f {
        val accessor = matrix4f as IMatrix4f
        return Vector4f(
            accessor.a00 * vector.x + accessor.a01 * vector.y + accessor.a02 * vector.z + accessor.a03 * vector.w,
            accessor.a10 * vector.x + accessor.a11 * vector.y + accessor.a12 * vector.z + accessor.a13 * vector.w,
            accessor.a20 * vector.x + accessor.a21 * vector.y + accessor.a22 * vector.z + accessor.a23 * vector.w,
            accessor.a30 * vector.x + accessor.a31 * vector.y + accessor.a32 * vector.z + accessor.a33 * vector.w
        )
    }

    @Priority(999) // don't draw above the gui
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventRender3D -> {
                hashMap.clear()
                if (!mode.isSelected(1))
                    return@Consumer
                for (entity in mc.world?.entities!!) {
                    if (!filter(entity)) continue

                    if (mc.options.perspective.isFirstPerson && entity == mc.player) continue

                    val prevPos = Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ)
                    val interp = prevPos.add(entity.pos.subtract(prevPos).multiply(mc.tickDelta.toDouble()))
                    val boundingBox = entity.boundingBox.offset(interp.subtract(entity.pos))

                    val corners = arrayOf(
                        Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ),
                        Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ),
                        Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ),
                        Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ),

                        Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ),
                        Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ),
                        Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ),
                        Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ)
                    )

                    var rectangle: Rectangle? = null

                    for (corner in corners) {
                        val projected = project(event.matrices.peek().positionMatrix, event.positionMatrix, corner) ?: continue
                        if (rectangle == null)
                            rectangle = Rectangle(projected.x, projected.y, projected.x, projected.y)
                        else {
                            if (rectangle.x > projected.x)
                                rectangle.x = projected.x

                            if (rectangle.y > projected.y)
                                rectangle.y = projected.y

                            if (rectangle.z < projected.x)
                                rectangle.z = projected.x

                            if (rectangle.w < projected.y)
                                rectangle.w = projected.y
                        }
                    }

                    if (rectangle != null)
                        hashMap[entity] = rectangle
                }
            }
            is EventRender2D -> {
                for (entry in hashMap.entries) {
                    TarasandeMain.get().managerESP?.renderBox(event.matrices, entry.key, entry.value)
                }
            }
        }
    }

    inner class Rectangle(var x: Double, var y: Double, var z: Double, var w: Double)
}