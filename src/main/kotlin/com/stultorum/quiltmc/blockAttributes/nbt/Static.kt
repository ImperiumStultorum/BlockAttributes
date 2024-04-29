package com.stultorum.quiltmc.blockAttributes.nbt

import com.stultorum.quiltmc.blunders.lang.Bool
import com.stultorum.quiltmc.blunders.lang.exceptions.AlreadyRegisteredException
import com.stultorum.quiltmc.blunders.lang.exceptions.NotRegisteredException
import net.minecraft.nbt.NbtCompound

// TODO should make Serializers generated as well. Can you create a partially generated file?
private val Serializers: MutableMap<Class<*>, CompoundSerializer<*>> = mutableMapOf(
    String    ::class.javaObjectType to StringCompoundSerializer(),
    Bool      ::class.javaObjectType to BooleanCompoundSerializer(),
    Byte      ::class.javaObjectType to ByteCompoundSerializer(),
    Short     ::class.javaObjectType to ShortCompoundSerializer(),
    Int       ::class.javaObjectType to IntCompoundSerializer(),
    Long      ::class.javaObjectType to LongCompoundSerializer(),
    Float     ::class.javaObjectType to FloatCompoundSerializer(),
    Double    ::class.javaObjectType to DoubleCompoundSerializer(),
    ByteArray ::class.javaObjectType to ByteArrayCompoundSerializer(),
    IntArray  ::class.javaObjectType to IntArrayCompoundSerializer(),
    LongArray ::class.javaObjectType to LongArrayCompoundSerializer()
)

fun getSerializer(clazz: Class<*>): CompoundSerializer<*> {
    ensureSerializerExistsFor(clazz)
    return Serializers[clazz]!!
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> getSerializer() = getSerializer(T::class.java) as CompoundSerializer<T>

fun serializerExistsFor(clazz: Class<*>): Bool = Serializers.containsKey(clazz)
fun ensureSerializerExistsFor(clazz: Class<*>) {
    if (!serializerExistsFor(clazz)) throw NotRegisteredException("CompoundSerializer<${clazz.canonicalName}>")
}
inline fun <reified T> serializerExistsFor() = serializerExistsFor(T::class.java)
inline fun <reified T> ensureSerializerExistsFor() = ensureSerializerExistsFor(T::class.java)

fun <T> registerSerializer(clazz: Class<T>, serializer: CompoundSerializer<T>) {
    if (Serializers.containsKey(clazz)) throw AlreadyRegisteredException("CompoundSerializer<${clazz.canonicalName}>")
    Serializers[clazz] = serializer
}

inline fun <reified T> toNbtCompound(obj: T): NbtCompound = getSerializer<T>().serialize(obj)
inline fun <reified T> fromNbtCompound(nbt: NbtCompound): T = getSerializer<T>().deserialize(nbt)
