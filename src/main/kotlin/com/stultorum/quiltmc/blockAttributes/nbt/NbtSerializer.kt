package com.stultorum.quiltmc.blockAttributes.nbt

import net.minecraft.nbt.NbtElement

abstract class NbtSerializer<T>() {
    abstract fun serialize(obj: T): NbtElement
    abstract fun deserialize(nbt: NbtElement): T
}
