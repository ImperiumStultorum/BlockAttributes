package com.stultorum.quiltmc.blockAttributes.locks

import com.stultorum.quiltmc.blunders.lang.Bool
import net.minecraft.util.Identifier
import java.util.*

interface IIdentifierPattern {
    fun matches(id: Identifier): Bool
    fun isSubsetOf(other: IIdentifierPattern): Bool
}

class GlobalPattern: IIdentifierPattern {
    override fun matches(id: Identifier): Bool = true
    override fun isSubsetOf(other: IIdentifierPattern): Bool = false
    override fun equals(other: Any?): Bool = other is GlobalPattern
    override fun hashCode(): Int = 261343141 // Random constant decided by fair dice roll (scuffed)
}

class NamespacePattern(internal val namespace: String): IIdentifierPattern {
    override fun matches(id: Identifier): Bool = id.namespace == namespace
    override fun isSubsetOf(other: IIdentifierPattern): Bool = other is GlobalPattern
    override fun equals(other: Any?): Bool = other is NamespacePattern && other.namespace == namespace
    override fun hashCode(): Int = namespace.hashCode()
}

class PrefixPattern(internal val namespace: String, internal val prefix: String): IIdentifierPattern {
    override fun matches(id: Identifier): Bool = id.namespace == namespace && id.path.startsWith(prefix)
    override fun isSubsetOf(other: IIdentifierPattern): Bool {
        return when (other) {
            is GlobalPattern -> true
            is NamespacePattern -> other.namespace == namespace
            else -> false
        }
    }
    override fun equals(other: Any?): Bool = other is PrefixPattern && other.namespace == namespace && other.prefix == prefix
    override fun hashCode(): Int = Objects.hash(namespace, prefix)
}

class ExactPattern(internal val id: Identifier): IIdentifierPattern {
    override fun matches(id: Identifier): Bool = id == this.id
    override fun isSubsetOf(other: IIdentifierPattern): Bool {
        return when (other) {
            is GlobalPattern -> true
            is NamespacePattern -> other.namespace == id.namespace
            is PrefixPattern -> other.namespace == id.namespace && id.path.startsWith(other.prefix)
            else -> false
        }
    }
    override fun equals(other: Any?): Bool = other is ExactPattern && other.id == id
    override fun hashCode(): Int = id.hashCode()
    
    constructor(namespace: String, path: String): this(Identifier(namespace, path))
}
