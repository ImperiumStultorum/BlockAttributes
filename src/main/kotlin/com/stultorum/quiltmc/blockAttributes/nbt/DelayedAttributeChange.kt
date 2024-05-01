package com.stultorum.quiltmc.blockAttributes.nbt

import com.stultorum.quiltmc.blockAttributes.getBlockAttributesNbt
import com.stultorum.quiltmc.blockAttributes.setBlockAttributesNbt
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DelayedAttributeChange(val pos: BlockPos) {
    private val changes: MutableMap<Identifier, MutableList<(NbtCompound) -> NbtCompound>> = HashMap()

    fun request(id: Identifier, change: (NbtCompound) -> NbtCompound) {
        changes.computeIfAbsent(id) { ArrayList() }.add(change)
    }
    fun apply(world: World) {
        val attrs = world.getBlockAttributesNbt(pos).toMutableMap()
        val iter = changes.iterator()
        while (iter.hasNext()) {
            val (id, changes) = iter.next()
            var compound = attrs[id] ?: NbtCompound()
            changes.forEach { change -> compound = change(compound) }
            iter.remove()
        }
        world.setBlockAttributesNbt(pos, attrs)
    }
}
