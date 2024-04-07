package com.stultorum.quiltmc.blockAttributes.nbt

import com.stultorum.quiltmc.blunders.lang.Bool
import com.stultorum.quiltmc.blunders.lang.exceptions.AlreadyRegisteredException
import com.stultorum.quiltmc.blunders.lang.exceptions.NotRegisteredException
import net.minecraft.nbt.*

private val Serializers: MutableMap<Class<*>, NbtSerializer<*>> = mutableMapOf(
    String    ::class.java to StringNbtSerializer(),
    Bool      ::class.java to BoolNbtSerializer(),
    Byte      ::class.java to ByteNbtSerializer(),
    Short     ::class.java to ShortNbtSerializer(),
    Int       ::class.java to IntNbtSerializer(),
    Long      ::class.java to LongNbtSerializer(),
    Float     ::class.java to FloatNbtSerializer(),
    Double    ::class.java to DoubleNbtSerializer(),
    ByteArray ::class.java to ByteArrayNbtSerializer(),
    IntArray  ::class.java to IntArrayNbtSerializer(),
    LongArray ::class.java to LongArrayNbtSerializer()
)

private val NbtTypeMap: MutableMap<Class<*>, Class<*>> = mutableMapOf(
    NbtString    ::class.java to String::class.java,
    NbtByte      ::class.java to Byte::class.java,
    NbtShort     ::class.java to Short::class.java,
    NbtInt       ::class.java to Int::class.java,
    NbtLong      ::class.java to Long::class.java,
    NbtFloat     ::class.java to Float::class.java,
    NbtDouble    ::class.java to Double::class.java,
    NbtByteArray ::class.java to ByteArray::class.java,
    NbtIntArray  ::class.java to IntArray::class.java,
    NbtLongArray ::class.java to LongArray::class.java,
)

public fun getSerializer(clazz: Class<*>): NbtSerializer<*> {
    ensureSerializerExistsFor(clazz)
    return Serializers[clazz] ?: Serializers[NbtTypeMap[clazz]!!]!!
}
public inline fun <reified T> getSerializer() = getSerializer(T::class.java) as NbtSerializer<T>

public fun serializerExistsFor(clazz: Class<*>): Bool {
    return Serializers.containsKey(clazz) || (NbtTypeMap.containsKey(clazz) && Serializers.containsKey(NbtTypeMap[clazz]))
}
public fun ensureSerializerExistsFor(clazz: Class<*>) {
    if (!serializerExistsFor(clazz)) throw NotRegisteredException("NbtSerializer<${clazz.canonicalName}>")
}
public inline fun <reified T> serializerExistsFor() = serializerExistsFor(T::class.java)
public inline fun <reified T> ensureSerializerExistsFor() = ensureSerializerExistsFor(T::class.java)

public fun <T> registerSerializer(clazz: Class<T>, nbtClazz: Class<*>, serializer: NbtSerializer<T>) {
    if (Serializers.containsKey(clazz)) throw AlreadyRegisteredException("NbtSerializer<${clazz.canonicalName}>")
    if (NbtTypeMap.containsKey(nbtClazz)) throw AlreadyRegisteredException("NbtSerializer<${nbtClazz.canonicalName}>")
    Serializers[clazz] = serializer
    NbtTypeMap[nbtClazz] = clazz
}

public inline fun <reified T> toNbt(obj: T): NbtElement = getSerializer<T>().serialize(obj)
public inline fun <reified T> fromNbt(nbt: NbtElement): T = getSerializer<T>().deserialize(nbt)
