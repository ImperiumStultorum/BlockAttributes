package com.stultorum.quiltmc.blockAttributes.nbt

import com.stultorum.quiltmc.blunders.lang.Bool
import net.minecraft.nbt.NbtByteArray
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtIntArray
import net.minecraft.nbt.NbtLongArray
import org.quiltmc.qkl.library.nbt.*

// Really should be compile-time generated, however since it will never change I just used regex101 replacement.
// not array: https://regex101.com/r/UCmT4b/1
//     array: https://regex101.com/r/dyaPpp/1

internal class StringNbtSerializer(): NbtSerializer<String>() {
    override fun serialize(obj: String): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): String = nbt.string
}
internal class BoolNbtSerializer(): NbtSerializer<Bool>() {
    override fun serialize(obj: Bool): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): Bool = nbt.boolean
}
internal class ByteNbtSerializer(): NbtSerializer<Byte>() {
    override fun serialize(obj: Byte): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): Byte = nbt.byte
}
internal class ShortNbtSerializer(): NbtSerializer<Short>() {
    override fun serialize(obj: Short): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): Short = nbt.short
}
internal class IntNbtSerializer(): NbtSerializer<Int>() {
    override fun serialize(obj: Int): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): Int = nbt.int
}
internal class LongNbtSerializer(): NbtSerializer<Long>() {
    override fun serialize(obj: Long): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): Long = nbt.long
}
internal class FloatNbtSerializer(): NbtSerializer<Float>() {
    override fun serialize(obj: Float): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): Float = nbt.float
}
internal class DoubleNbtSerializer(): NbtSerializer<Double>() {
    override fun serialize(obj: Double): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): Double = nbt.double
}
internal class ByteArrayNbtSerializer(): NbtSerializer<ByteArray>() {
    override fun serialize(obj: ByteArray): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): ByteArray = (nbt as NbtByteArray).byteArray
}
internal class IntArrayNbtSerializer(): NbtSerializer<IntArray>() {
    override fun serialize(obj: IntArray): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): IntArray = (nbt as NbtIntArray).intArray
}
internal class LongArrayNbtSerializer(): NbtSerializer<LongArray>() {
    override fun serialize(obj: LongArray): NbtElement = obj.nbt
    override fun deserialize(nbt: NbtElement): LongArray = (nbt as NbtLongArray).longArray
}
