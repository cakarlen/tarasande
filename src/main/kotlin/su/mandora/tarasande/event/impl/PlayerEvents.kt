package su.mandora.tarasande.event.impl

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import su.mandora.tarasande.event.Event

class EventUpdate(val state: State) : Event(state == State.PRE) {
    enum class State {
        PRE, PRE_PACKET, POST
    }
}

class EventVelocity(var velocityX: Double, var velocityY: Double, var velocityZ: Double, val packet: Packet) : Event(true) {
    enum class Packet {
        VELOCITY, EXPLOSION
    }
}

class EventJump(var yaw: Float, val state: State) : Event(state == State.PRE) {
    enum class State {
        PRE, POST
    }
}

class EventMovement : Event {
    val entity: Entity
    var dirty = false
        private set
    var velocity: Vec3d = Vec3d.ZERO
        set(value) {
            field = value
            dirty = true
        }

    constructor(entity: Entity, velocity: Vec3d) : super(false) {
        this.entity = entity
        this.velocity = velocity
        this.dirty = false
    }

}

class EventAttackEntity(val entity: Entity, val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventKeepSprint(var sprinting: Boolean) : Event(false)
class EventAttack : Event(false) {
    var dirty = false
        set(value) {
            if (field && !value)
                error(javaClass.name + " is already dirty")
            field = value
        }
}

class EventStep : Event {
    var stepHeight = 0F
        set(value) {
            if (state == State.POST)
                error("stepHeight can't be modified during " + State.POST.name)
            field = value
        }
    val state: State

    constructor(stepHeight: Float, state: State) : super(false) {
        this.stepHeight = stepHeight
        this.state = state
    }

    enum class State {
        PRE, POST
    }
}

class EventBlockCollision(val state: BlockState, val pos: BlockPos, val entity: Entity) : Event(true)
class EventEntityFlag(val entity: Entity, val flag: Int, var enabled: Boolean) : Event(false)
class EventCollisionShape(val pos: BlockPos, var collisionShape: VoxelShape) : Event(false)
class EventBoundingBoxOverride(val entity: Entity, var boundingBox: Box) : Event(false)
class EventChat(val chatMessage: String) : Event(true)
class EventSwing(var hand: Hand) : Event(true)
class EventEntityHurt(val entity: Entity) : Event(false)
class EventIsWalkingForward(var walksForward: Boolean) : Event(false)
class EventInteractBlock(val hand: Hand, val hitResult: BlockHitResult) : Event(cancellable = true)
class EventVelocityMultiplier : Event {
    var dirty = false
        private set

    val block: Block
    var velocityMultiplier = 0.0
        set(value) {
            field = value
            dirty = true
        }

    constructor(block: Block, velocityMultiplier: Double) : super(false) {
        this.block = block
        this.velocityMultiplier = velocityMultiplier
        this.dirty = false
    }
}
