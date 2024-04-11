package com.stultorum.quiltmc.blockAttributes

import com.stultorum.quiltmc.blockAttributes.mixinfs.IAttributeWorldChunk
import com.stultorum.quiltmc.blockAttributes.nbt.fromNbtCompound
import com.stultorum.quiltmc.blockAttributes.nbt.getSerializer
import com.stultorum.quiltmc.blockAttributes.nbt.toNbtCompound
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk

typealias BlockAttributes = Map<Identifier, NbtCompound>


fun World.getBlockAttributesNbt(pos: BlockPos): BlockAttributes {
    if (isOutOfHeightLimit(pos) || (!isClient && Thread.currentThread() != thread)) return HashMap()
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
    if (isOutOfHeightLimit(pos) || (!isClient && Thread.currentThread() != thread)) return
    getWorldChunk(pos).setBlockAttributeNbt(pos, id, obj)
}

fun World.setBlockAttributesNbt(pos: BlockPos, attributes: BlockAttributes) {
    if (isOutOfHeightLimit(pos) || (!isClient && Thread.currentThread() != thread)) return
    getWorldChunk(pos).setBlockAttributesNbt(pos, attributes)
}

// TODO add forwarding for events

// Chunk -> IAttributeWorldChunk util
@Suppress("NOTHING_TO_INLINE") // Literally just done because this simple of a function not being inlined makes less sense to me then the opposite.
inline fun Chunk.asAttributeChunk() = (this as IAttributeWorldChunk)
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
