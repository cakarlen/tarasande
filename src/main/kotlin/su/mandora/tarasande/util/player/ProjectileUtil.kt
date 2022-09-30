package su.mandora.tarasande.util.player

import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.BowItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.util.math.random.RandomSplitter
import su.mandora.tarasande.mixin.accessor.*
import su.mandora.tarasande.util.extension.times
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import java.util.function.BiConsumer

object ProjectileUtil {

    internal val projectileItems = arrayOf(ProjectileItem(Items.BOW.javaClass, EntityType.ARROW, true) { stack, persistentProjectileEntity ->
        val velocity = BowItem.getPullProgress(if (MinecraftClient.getInstance().player?.isUsingItem!!) MinecraftClient.getInstance().player?.itemUseTime!! else stack.maxUseTime).toDouble()
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0f, (velocity * 3.0).toFloat(), 1.0f)
    }, ProjectileItem(Items.SNOWBALL.javaClass, EntityType.SNOWBALL, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0f, 1.5f, 1.0f)
    }, ProjectileItem(Items.EGG.javaClass, EntityType.EGG, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0f, 1.5f, 1.0f)
    }, ProjectileItem(Items.ENDER_PEARL.javaClass, EntityType.ENDER_PEARL, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0f, 1.5f, 1.0f)
    }, ProjectileItem(Items.EXPERIENCE_BOTTLE.javaClass, EntityType.EXPERIENCE_BOTTLE, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, -20.0f, 0.7f, 1.0f)
    }, ProjectileItem(Items.SPLASH_POTION.javaClass, EntityType.POTION, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, -20.0f, 0.5f, 1.0f)
    }, ProjectileItem(Items.TRIDENT.javaClass, EntityType.TRIDENT, true) { item, persistentProjectileEntity ->
        val riptide = EnchantmentHelper.getRiptide(item)
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0f, 2.5f + riptide.toFloat() * 0.5f, 1.0f)
    }, ProjectileItem(Items.FISHING_ROD.javaClass, EntityType.FISHING_BOBBER, false) { _, persistentProjectileEntity ->
        val f = MinecraftClient.getInstance().player?.pitch!!
        val g = MinecraftClient.getInstance().player?.yaw!!
        val h = MathHelper.cos(Math.toRadians(-g.toDouble()).toFloat() - Math.PI.toFloat())
        val i = MathHelper.sin(Math.toRadians(-g.toDouble()).toFloat() - Math.PI.toFloat())
        val j = -MathHelper.cos(Math.toRadians(-f.toDouble()).toFloat())
        val k = MathHelper.sin(Math.toRadians(-f.toDouble()).toFloat())

        persistentProjectileEntity.refreshPositionAndAngles(persistentProjectileEntity.x - i.toDouble() * 0.3, persistentProjectileEntity.y + 0.1, persistentProjectileEntity.z - h.toDouble() * 0.3, f, g)

        var vec3d = Vec3d(-i.toDouble(), MathHelper.clamp(-(k / j), -5.0f, 5.0f).toDouble(), -h.toDouble())
        val m = vec3d.length()
        vec3d = vec3d.multiply(0.6 / m + 0.5 + (persistentProjectileEntity as IEntity).tarasande_getRandom().nextGaussian() * 0.0045, 0.6 / m + 0.5 + (persistentProjectileEntity as IEntity).tarasande_getRandom().nextGaussian() * 0.0045, 0.6 / m + 0.5 + (persistentProjectileEntity as IEntity).tarasande_getRandom().nextGaussian() * 0.0045)

        persistentProjectileEntity.velocity = vec3d
        val rotation = RotationUtil.getRotations(vec3d)
        persistentProjectileEntity.also {
            it.yaw = rotation.yaw
            it.prevYaw = rotation.yaw
        }
        persistentProjectileEntity.also {
            it.pitch = rotation.pitch
            it.prevPitch = rotation.pitch
        }
    }, ProjectileItem(Items.CROSSBOW.javaClass, EntityType.ARROW, true) { stack, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0f, (stack.item as ICrossbowItem).tarasande_invokeGetSpeed(stack), 1.0f)

    })

    fun predict(itemStack: ItemStack, rotation: Rotation?, predictVelocity: Boolean): ArrayList<Vec3d> {
        val projectileItem = projectileItems.first { it.isSame(itemStack.item) }
        (MinecraftClient.getInstance().world as IWorld).tarasande_setIsClient(false)
        val path = ArrayList<Vec3d>()
        var collided = false

        @Suppress("UNCHECKED_CAST")
        val persistentProjectileEntity = object : PersistentProjectileEntity(projectileItem.entityType as EntityType<PersistentProjectileEntity> /* This just shows how bad minecrafts code base is */, MinecraftClient.getInstance().player, MinecraftClient.getInstance().world) {
            override fun asItemStack(): ItemStack? = null
            override fun onEntityHit(entityHitResult: EntityHitResult?) {
                collided = true
            }

            override fun onHit(target: LivingEntity?) {
                collided = true
            }

            override fun tick() {
                super.tick()
                if (!projectileItem.persistent) addVelocity(0.0, 0.02, 0.0)
                if (projectileItem.entityType == EntityType.FISHING_BOBBER) velocity *= 0.92
            }
        }
        persistentProjectileEntity.setPosition(MinecraftClient.getInstance().player?.getLerpedPos(MinecraftClient.getInstance().tickDelta)?.add(0.0, MinecraftClient.getInstance().player?.standingEyeHeight!! - 0.1, 0.0))
        (persistentProjectileEntity as IEntity).tarasande_setRandom(object : Random {
            override fun split(): Random {
                return this
            }

            override fun nextSplitter(): RandomSplitter {
                val this2 = this // Kotlin is great
                return object : RandomSplitter {
                    override fun split(seed: String?): Random {
                        return this2
                    }

                    override fun split(x: Int, y: Int, z: Int): Random {
                        return this2
                    }

                    override fun addDebugInfo(info: StringBuilder?) {
                    }
                }
            }

            override fun setSeed(seed: Long) {
            }

            override fun nextInt(): Int {
                return 0
            }

            override fun nextInt(bound: Int): Int {
                return 0
            }

            override fun nextLong(): Long {
                return 0L
            }

            override fun nextBoolean(): Boolean {
                return false
            }

            override fun nextFloat(): Float {
                return 0.0F
            }

            override fun nextDouble(): Double {
                return 0.0
            }

            override fun nextGaussian(): Double {
                return 0.0
            }
        })

        val prevRotation = Rotation(MinecraftClient.getInstance().player!!)
        val prevVelocity = Vec3d(0.0, 0.0, 0.0).also { (it as IVec3d).tarasande_copy(MinecraftClient.getInstance().player?.velocity) }
        if (rotation != null) {
            MinecraftClient.getInstance().player?.yaw = rotation.yaw
            MinecraftClient.getInstance().player?.pitch = rotation.pitch
        }
        if (!predictVelocity) {
            MinecraftClient.getInstance().player?.velocity = Vec3d.ZERO
        }
        projectileItem.setupRoutine.accept(itemStack, persistentProjectileEntity)
        MinecraftClient.getInstance().player?.velocity = prevVelocity
        MinecraftClient.getInstance().player?.yaw = prevRotation.yaw
        MinecraftClient.getInstance().player?.pitch = prevRotation.pitch
        while (!collided) {
            val prevParticlesEnabled = (MinecraftClient.getInstance().particleManager as IParticleManager).tarasande_areParticlesEnabled() // race conditions :c
            (MinecraftClient.getInstance().particleManager as IParticleManager).tarasande_setParticlesEnabled(false)
            persistentProjectileEntity.tick()
            (MinecraftClient.getInstance().particleManager as IParticleManager).tarasande_setParticlesEnabled(prevParticlesEnabled)
            if (persistentProjectileEntity.pos.let { it.y < MinecraftClient.getInstance().world?.bottomY!! || it == path.lastOrNull() }) break
            path.add(persistentProjectileEntity.pos)
        }
        (MinecraftClient.getInstance().world as IWorld).tarasande_setIsClient(true)
        return path
    }

    class ProjectileItem(val item: Class<Item>, val entityType: EntityType<*>, val persistent: Boolean, val setupRoutine: BiConsumer<ItemStack, PersistentProjectileEntity>) {
        fun isSame(item: Item) = this.item.isInstance(item)
    }
}