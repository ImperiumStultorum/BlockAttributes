@file:Suppress("unused")

package com.stultorum.quiltmc.blockAttributes

import com.stultorum.quiltmc.blockAttributes.mixinfs.IAttributeWorldChunk
import com.stultorum.quiltmc.blockAttributes.mixinfs.IAttributeWorldChunk.AttributeEventType
import com.stultorum.quiltmc.blockAttributes.nbt.DelayedAttributeChange
import com.stultorum.quiltmc.blockAttributes.nbt.fromNbtCompound
import com.stultorum.quiltmc.blockAttributes.nbt.getSerializer
import com.stultorum.quiltmc.blockAttributes.nbt.toNbtCompound
import com.stultorum.quiltmc.blunders.lang.Bool
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import java.util.concurrent.CountDownLatch

typealias BlockAttributes = Map<Identifier, NbtCompound>

private data class DelayedChange(var lock: CountDownLatch, val change: DelayedAttributeChange)
private val delayedChanges = HashMap<BlockPos, DelayedChange>()

fun World.getBlockAttributesNbt(pos: BlockPos): BlockAttributes {
    if (!safeToQueryBlock(pos)) return HashMap()
    delayedChanges[pos]?.lock?.await()
    return getWorldChunk(pos).getBlockAttributesNbt(pos)
}

inline fun <reified T> World.getBlockAttribute(pos: BlockPos, id: Identifier): T? {
    return fromNbtCompound<T>(getBlockAttributesNbt(pos)[id] ?: return null)
}

fun World.getBlockAttributeNbt(pos: BlockPos, id: Identifier): NbtCompound? = getBlockAttributesNbt(pos)[id]

inline fun <reified T> World.setBlockAttribute(pos: BlockPos, id: Identifier, obj: T?) = setBlockAttributeNbt(pos, id, toNbtCompound(obj))

/** 
 * Pass in null to remove.
 */
fun World.setBlockAttributeNbt(pos: BlockPos, id: Identifier, obj: NbtCompound?) {
    if (!safeToQueryBlock(pos)) return
    delayedChanges[pos]?.lock?.await()
    getWorldChunk(pos).setBlockAttributeNbt(pos, id, obj)
}

fun World.setBlockAttributesNbt(pos: BlockPos, attributes: BlockAttributes) {
    if (!safeToQueryBlock(pos)) return
    delayedChanges[pos]?.lock?.await()
    getWorldChunk(pos).setBlockAttributesNbt(pos, attributes)
}

fun World.requestAttributeChange(pos: BlockPos, id: Identifier, change: (NbtCompound) -> NbtCompound) {
    if (!safeToQueryBlock(pos)) return
    if (!delayedChanges.containsKey(pos)) {
        delayedChanges[pos] = DelayedChange(CountDownLatch(1), DelayedAttributeChange(pos))
    }
    val delayedChange = delayedChanges[pos]!!
    if (delayedChange.lock.count == 0L) delayedChange.lock = CountDownLatch(1)
    delayedChange.change.request(id, change)
}

fun World.applyAttributeChange(pos: BlockPos) {
    if (!safeToQueryBlock(pos)) return
    delayedChanges[pos]!!.change.apply(this)
    delayedChanges[pos]!!.lock.countDown()
}

fun World.addAttributeListener(type: AttributeEventType, pos: BlockPos, callback: () -> Unit) {
    if (!safeToQueryBlock(pos)) return
    getWorldChunk(pos).addAttributeListener(type, pos, callback)
}

fun World.removeAttributeListener(type: AttributeEventType, pos: BlockPos, callback: () -> Unit) {
    if (!safeToQueryBlock(pos)) return
    getWorldChunk(pos).removeAttributeListener(type, callback)
}

private fun World.safeToQueryBlock(pos: BlockPos): Bool = isOutOfHeightLimit(pos) || (!isClient && Thread.currentThread() != thread)

// Chunk -> IAttributeWorldChunk util
@Suppress("NOTHING_TO_INLINE") // Literally just done because this simple of a function not being inlined makes less sense to me then the opposite.
internal inline fun Chunk.asAttributeChunk() = (this as IAttributeWorldChunk)
inline fun <reified T: Any> Chunk.setBlockAttribute(pos: BlockPos, id: Identifier, obj: T?) = setBlockAttributeNbt(pos, id, if (obj == null) null else getSerializer<T>().serialize(obj))
inline fun <reified T: Any> Chunk.getBlockAttribute(pos: BlockPos, id: Identifier): T? {
    val nbt = getBlockAttributeNbt(pos, id) ?: return null
    return getSerializer<T>().deserialize(nbt)
}

// Chunk -> IAttributeWorldChunk forwarding 
fun Chunk.getBlockAttributesNbt(pos: BlockPos): BlockAttributes = asAttributeChunk().getBlockAttributes(pos)
fun Chunk.getBlockAttributeNbt(pos: BlockPos, id: Identifier): NbtCompound? = asAttributeChunk().getBlockAttribute(pos, id)
fun Chunk.setBlockAttributesNbt(pos: BlockPos, attributes: BlockAttributes?): Unit = if (attributes == null) clearBlockAttributes(pos) else asAttributeChunk().setBlockAttributes(pos, attributes)
fun Chunk.setBlockAttributeNbt(pos: BlockPos, id: Identifier, nbt: NbtCompound?): Unit = if (nbt == null) removeBlockAttribute(pos, id) else asAttributeChunk().setBlockAttribute(pos, id, nbt)
fun Chunk.removeBlockAttribute(pos: BlockPos, id: Identifier): Unit = asAttributeChunk().removeBlockAttribute(pos, id)
fun Chunk.clearBlockAttributes(pos: BlockPos): Unit = asAttributeChunk().clearBlockAttributes(pos)
fun Chunk.addAttributeListener(type: AttributeEventType, pos: BlockPos, callback: () -> Unit) = asAttributeChunk().addAttributeListener(type, pos, callback)
fun Chunk.addAttributeListener(type: AttributeEventType, callback: (BlockPos) -> Unit) = asAttributeChunk().addAttributeListener(type, callback)
fun Chunk.addAttributeListener(type: AttributeEventType, condition: (BlockPos) -> Bool, callback: () -> Unit) = addAttributeListener(type, condition) { _ -> callback() }
fun Chunk.addAttributeListener(type: AttributeEventType, condition: (BlockPos) -> Bool, callback: (BlockPos) -> Unit) = asAttributeChunk().addAttributeListener(type, condition, callback)
fun Chunk.removeAttributeListener(type: AttributeEventType, callback: () -> Unit) = asAttributeChunk().removeAttributeListener(type, callback)
fun Chunk.removeAttributeListener(type: AttributeEventType, callback: (BlockPos) -> Unit) = asAttributeChunk().removeAttributeListener(type, callback)
