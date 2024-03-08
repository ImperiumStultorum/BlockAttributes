package com.stultorum.quiltmc.blockAttributes

import com.stultorum.quiltmc.blockAttributes.mixins.infs.IAttributeWorldChunk
import com.stultorum.quiltmc.blockAttributes.nbt.fromNbt
import com.stultorum.quiltmc.blockAttributes.nbt.getSerializer
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtNull
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.WorldChunk

typealias BlockAttributes = HashMap<Identifier, NbtElement>


public fun World.getBlockAttributesNbt(pos: BlockPos): BlockAttributes {
    if (isOutOfHeightLimit(pos) || (!isClient && Thread.currentThread() != thread)) return BlockAttributes()
    return getWorldChunk(pos).getBlockAttributesNbt(pos)
}

public inline fun <reified T> World.getBlockAttribute(pos: BlockPos, id: Identifier): T? {
    return fromNbt<T>(getBlockAttributesNbt(pos)[id] ?: return null)
}

// WorldChunk -> IAttributeWorldChunk util
public inline fun WorldChunk.asAttributeChunk() = (this as IAttributeWorldChunk)
public inline fun <reified T: Any> WorldChunk.setBlockAttribute(pos: BlockPos, id: Identifier, obj: T?) = setBlockAttributeNbt(pos, id, if (obj == null) null else getSerializer<T>().serialize(obj))
public inline fun <reified T: Any> WorldChunk.getBlockAttribute(pos: BlockPos, id: Identifier): T? {
    val nbt = getBlockAttributeNbt(pos, id) ?: return null
    if (nbt is NbtNull) return null
    return getSerializer<T>().deserialize(nbt)
}

// WorldChunk -> IAttributeWorldChunk forwarding 
public inline fun WorldChunk.getBlockAttributesNbt(pos: BlockPos) = asAttributeChunk().getBlockAttributes(pos)
public inline fun WorldChunk.getBlockAttributeNbt(pos: BlockPos, id: Identifier) = asAttributeChunk().getBlockAttribute(pos, id)
public inline fun WorldChunk.setBlockAttributesNbt(pos: BlockPos, attributes: BlockAttributes?) = if (attributes == null) clearBlockAttributes(pos) else asAttributeChunk().setBlockAttributes(pos, attributes);
public inline fun WorldChunk.setBlockAttributeNbt(pos: BlockPos, id: Identifier, nbt: NbtElement?) = if (nbt == null) removeBlockAttribute(pos, id) else asAttributeChunk().setBlockAttribute(pos, id, nbt)
public inline fun WorldChunk.removeBlockAttribute(pos: BlockPos, id: Identifier) = asAttributeChunk().removeBlockAttribute(pos, id)
public inline fun WorldChunk.clearBlockAttributes(pos: BlockPos) = asAttributeChunk().clearBlockAttributes(pos)
