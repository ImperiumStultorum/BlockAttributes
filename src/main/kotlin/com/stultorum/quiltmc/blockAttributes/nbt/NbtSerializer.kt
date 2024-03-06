package com.stultorum.quiltmc.blockAttributes.nbt

import net.minecraft.nbt.NbtElement

public open abstract class NbtSerializer<T>() {
    public abstract fun serialize(obj: T): NbtElement
    public abstract fun deserialize(nbt: NbtElement): T
}
