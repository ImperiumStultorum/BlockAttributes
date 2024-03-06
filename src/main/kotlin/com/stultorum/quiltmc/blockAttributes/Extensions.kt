package com.stultorum.quiltmc.blockAttributes

import com.stultorum.quiltmc.blockAttributes.nbt.fromNbt
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.WorldChunk

typealias BlockAttributes = HashMap<Identifier, NbtElement>

public fun World.getBlockAttributes(pos: BlockPos): BlockAttributes {
    if (isOutOfHeightLimit(pos) || (!isClient && Thread.currentThread() != thread)) return BlockAttributes()
    return getWorldChunk(pos).getBlockAttributes(pos)
}

public inline fun <reified T> World.getBlockAttribute(pos: BlockPos, id: Identifier): T? {
    return fromNbt<T>(getBlockAttributes(pos)[id] ?: return null)
}

public fun WorldChunk.getBlockAttributes(pos: BlockPos): BlockAttributes {
    TODO("Pain")
}

public fun WorldChunk.loadBlockAttributes(pos: BlockPos, compound: NbtCompound): BlockAttributes {
    TODO("Pain")
}
