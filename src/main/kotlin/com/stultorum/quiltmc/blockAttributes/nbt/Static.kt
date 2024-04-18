package com.stultorum.quiltmc.blockAttributes.nbt

import com.stultorum.quiltmc.blunders.lang.Bool
import com.stultorum.quiltmc.blunders.lang.exceptions.AlreadyRegisteredException
import com.stultorum.quiltmc.blunders.lang.exceptions.NotRegisteredException
import net.minecraft.nbt.NbtCompound

// TODO should make Serializers generated as well. Can you create a partially generated file?
private val Serializers: MutableMap<Class<*>, CompoundSerializer<*>> = mutableMapOf(
    String    ::class.java to StringCompoundSerializer(),
    Bool      ::class.java to BooleanCompoundSerializer(),
    Byte      ::class.java to ByteCompoundSerializer(),
    Short     ::class.java to ShortCompoundSerializer(),
    Int       ::class.java to IntCompoundSerializer(),
    Long      ::class.java to LongCompoundSerializer(),
    Float     ::class.java to FloatCompoundSerializer(),
    Double    ::class.java to DoubleCompoundSerializer(),
    ByteArray ::class.java to ByteArrayCompoundSerializer(),
    IntArray  ::class.java to IntArrayCompoundSerializer(),
    LongArray ::class.java to LongArrayCompoundSerializer()
)

fun getSerializer(clazz: Class<*>): CompoundSerializer<*> {
    ensureSerializerExistsFor(clazz)
    return Serializers[clazz]!!
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> getSerializer() = getSerializer(T::class.java) as CompoundSerializer<T>

fun serializerExistsFor(clazz: Class<*>): Bool = Serializers.containsKey(clazz)
fun ensureSerializerExistsFor(clazz: Class<*>) {
    if (!serializerExistsFor(clazz)) throw NotRegisteredException("NbtSerializer<${clazz.canonicalName}>")
}
inline fun <reified T> serializerExistsFor() = serializerExistsFor(T::class.java)
inline fun <reified T> ensureSerializerExistsFor() = ensureSerializerExistsFor(T::class.java)

fun <T> registerSerializer(clazz: Class<T>, serializer: CompoundSerializer<T>) {
    if (Serializers.containsKey(clazz)) throw AlreadyRegisteredException("NbtSerializer<${clazz.canonicalName}>")
    Serializers[clazz] = serializer
}

inline fun <reified T> toNbtCompound(obj: T): NbtCompound = getSerializer<T>().serialize(obj)
inline fun <reified T> fromNbtCompound(nbt: NbtCompound): T = getSerializer<T>().deserialize(nbt)
