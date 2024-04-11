package com.stultorum.quiltmc.blockAttributes.nbt

import com.stultorum.quiltmc.blunders.lang.Bool
import org.quiltmc.qkl.library.nbt.NbtCompound
import org.quiltmc.qkl.library.nbt.nbt
import net.minecraft.nbt.NbtCompound as NbtCompoundType

// Really should be compile-time generated, however since it will never change I just used regex101 replacement.
// https://regex101.com/r/UCmT4b/2

internal class StringNbtSerializer : NbtSerializer<String>() {
    override fun serialize(obj: String): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): String = nbt.getString("val")
}
class BoolNbtSerializer : NbtSerializer<Bool>() {
    override fun serialize(obj: Bool): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): Bool = nbt.getBoolean("val")
}
class ByteNbtSerializer : NbtSerializer<Byte>() {
    override fun serialize(obj: Byte): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): Byte = nbt.getByte("val")
}
class ShortNbtSerializer : NbtSerializer<Short>() {
    override fun serialize(obj: Short): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): Short = nbt.getShort("val")
}
class IntNbtSerializer : NbtSerializer<Int>() {
    override fun serialize(obj: Int): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): Int = nbt.getInt("val")
}
class LongNbtSerializer : NbtSerializer<Long>() {
    override fun serialize(obj: Long): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): Long = nbt.getLong("val")
}
class FloatNbtSerializer : NbtSerializer<Float>() {
    override fun serialize(obj: Float): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): Float = nbt.getFloat("val")
}
class DoubleNbtSerializer : NbtSerializer<Double>() {
    override fun serialize(obj: Double): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): Double = nbt.getDouble("val")
}
class ByteArrayNbtSerializer : NbtSerializer<ByteArray>() {
    override fun serialize(obj: ByteArray): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): ByteArray = nbt.getByteArray("val")
}
class IntArrayNbtSerializer : NbtSerializer<IntArray>() {
    override fun serialize(obj: IntArray): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): IntArray = nbt.getIntArray("val")
}
class LongArrayNbtSerializer : NbtSerializer<LongArray>() {
    override fun serialize(obj: LongArray): NbtCompoundType = NbtCompound(Pair("val", obj.nbt))
    override fun deserialize(nbt: NbtCompoundType): LongArray = nbt.getLongArray("val")
}
