package com.stultorum.quiltmc.blockAttributes.nbt

import com.stultorum.quiltmc.blunders.lang.Bool
import com.stultorum.quiltmc.blunders.lang.exceptions.AlreadyRegisteredException
import com.stultorum.quiltmc.blunders.lang.exceptions.NotRegisteredException
import net.minecraft.nbt.NbtElement

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

public fun getSerializer(clazz: Class<*>) = Serializers[clazz] ?: NotRegisteredException("NbtSerializer<${clazz.canonicalName}>")
public inline fun <reified T> getSerializer() = getSerializer(T::class.java) as NbtSerializer<T>

public fun serializerExistsFor(clazz: Class<*>) = Serializers.containsKey(clazz)
public fun ensureSerializerExistsFor(clazz: Class<*>) {
    if (!serializerExistsFor(clazz)) throw NotRegisteredException("NbtSerializer<${clazz.canonicalName}>")
}
public inline fun <reified T> serializerExistsFor() = serializerExistsFor(T::class.java)
public inline fun <reified T> ensureSerializerExistsFor() = ensureSerializerExistsFor(T::class.java)

public fun <T> registerSerializer(clazz: Class<T>, serializer: NbtSerializer<T>) {
    Serializers.putIfAbsent(clazz, serializer) ?: throw AlreadyRegisteredException("NbtSerializer<${clazz.canonicalName}>")
}

public inline fun <reified T> toNbt(obj: T): NbtElement = getSerializer<T>().serialize(obj)
public inline fun <reified T> fromNbt(nbt: NbtElement): T = getSerializer<T>().deserialize(nbt)
