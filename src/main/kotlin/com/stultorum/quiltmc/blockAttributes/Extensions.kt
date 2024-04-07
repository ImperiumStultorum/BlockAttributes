package com.stultorum.quiltmc.blockAttributes

import com.stultorum.quiltmc.blockAttributes.mixinfs.IAttributeWorldChunk
import com.stultorum.quiltmc.blockAttributes.nbt.fromNbt
import com.stultorum.quiltmc.blockAttributes.nbt.getSerializer
import com.stultorum.quiltmc.blockAttributes.nbt.toNbt
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtNull
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk

typealias BlockAttributes = HashMap<Identifier, NbtElement>


public fun World.getBlockAttributesNbt(pos: BlockPos): BlockAttributes {
    if (isOutOfHeightLimit(pos) || (!isClient && Thread.currentThread() != thread)) return BlockAttributes()
    return getWorldChunk(pos).getBlockAttributesNbt(pos)
}

public inline fun <reified T> World.getBlockAttribute(pos: BlockPos, id: Identifier): T? {
    return fromNbt<T>(getBlockAttributesNbt(pos)[id] ?: return null)
}

public fun World.getBlockAttribute(pos: BlockPos, id: Identifier): NbtElement? = getBlockAttributesNbt(pos)[id]

public inline fun <reified T> World.setBlockAttribute(pos: BlockPos, id: Identifier, obj: T?) = setBlockAttributeNbt(pos, id, toNbt(obj))

/** 
 * Pass in null to remove. 
 * 
 * Pass in NbtNull to set to null 
 */
public fun World.setBlockAttributeNbt(pos: BlockPos, id: Identifier, obj: NbtElement?) {
    if (isOutOfHeightLimit(pos) || (!isClient && Thread.currentThread() != thread)) return
    getWorldChunk(pos).setBlockAttributeNbt(pos, id, obj)
}

// Chunk -> IAttributeWorldChunk util
public inline fun Chunk.asAttributeChunk() = (this as IAttributeWorldChunk)
public inline fun <reified T: Any> Chunk.setBlockAttribute(pos: BlockPos, id: Identifier, obj: T?) = setBlockAttributeNbt(pos, id, if (obj == null) null else getSerializer<T>().serialize(obj))
public inline fun <reified T: Any> Chunk.getBlockAttribute(pos: BlockPos, id: Identifier): T? {
    val nbt = getBlockAttributeNbt(pos, id) ?: return null
    if (nbt is NbtNull) return null
    return getSerializer<T>().deserialize(nbt)
}

// Chunk -> IAttributeWorldChunk forwarding 
public inline fun Chunk.getBlockAttributesNbt(pos: BlockPos) = asAttributeChunk().getBlockAttributes(pos)
public inline fun Chunk.getBlockAttributeNbt(pos: BlockPos, id: Identifier) = asAttributeChunk().getBlockAttribute(pos, id)
public inline fun Chunk.setBlockAttributesNbt(pos: BlockPos, attributes: BlockAttributes?) = if (attributes == null) clearBlockAttributes(pos) else asAttributeChunk().setBlockAttributes(pos, attributes)
public inline fun Chunk.setBlockAttributeNbt(pos: BlockPos, id: Identifier, nbt: NbtElement?) = if (nbt == null) removeBlockAttribute(pos, id) else asAttributeChunk().setBlockAttribute(pos, id, nbt)
public inline fun Chunk.removeBlockAttribute(pos: BlockPos, id: Identifier) = asAttributeChunk().removeBlockAttribute(pos, id)
public inline fun Chunk.clearBlockAttributes(pos: BlockPos) = asAttributeChunk().clearBlockAttributes(pos)

public fun NbtElement.toDebugString(): String = getSerializer(this::class.java).deserialize(this).toString()
