package com.stultorum.quiltmc.blockAttributes.nbt

import net.minecraft.nbt.NbtCompound

abstract class CompoundSerializer<T> {
    abstract fun serialize(obj: T): NbtCompound
    abstract fun deserialize(nbt: NbtCompound): T
}
