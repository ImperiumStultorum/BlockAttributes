package com.stultorum.quiltmc.blockAttributes.locks

import com.stultorum.quiltmc.blunders.lang.Bool
import net.minecraft.util.Identifier

interface IIdentifierPattern {
    fun matches(id: Identifier): Bool
    fun isIdenticalTo(other: IIdentifierPattern): Bool
    fun isSubsetOf(other: IIdentifierPattern): Bool
}

class GlobalPattern: IIdentifierPattern {
    override fun matches(id: Identifier): Bool = true
    override fun isIdenticalTo(other: IIdentifierPattern): Bool = other is GlobalPattern
    override fun isSubsetOf(other: IIdentifierPattern): Bool = false
}

class NamespacePattern(internal val namespace: String): IIdentifierPattern {
    override fun matches(id: Identifier): Bool = id.namespace == namespace
    override fun isIdenticalTo(other: IIdentifierPattern): Bool = other is NamespacePattern && other.namespace == namespace
    override fun isSubsetOf(other: IIdentifierPattern): Bool = other is GlobalPattern
}

class PrefixPattern(internal val namespace: String, internal val prefix: String): IIdentifierPattern {
    override fun matches(id: Identifier): Bool = id.namespace == namespace && id.path.startsWith(prefix)
    override fun isIdenticalTo(other: IIdentifierPattern): Bool = other is PrefixPattern && other.namespace == namespace && other.prefix == prefix
    override fun isSubsetOf(other: IIdentifierPattern): Bool {
        return when (other) {
            is GlobalPattern -> true
            is NamespacePattern -> other.namespace == namespace
            else -> false
        }
    }
}

class ExactPattern(internal val id: Identifier): IIdentifierPattern {
    override fun matches(id: Identifier): Bool = id == this.id
    override fun isIdenticalTo(other: IIdentifierPattern): Bool = other is ExactPattern && other.id == id
    override fun isSubsetOf(other: IIdentifierPattern): Bool {
        return when (other) {
            is GlobalPattern -> true
            is NamespacePattern -> other.namespace == id.namespace
            is PrefixPattern -> other.namespace == id.namespace && id.path.startsWith(other.prefix)
            else -> false
        }
    }
    
    constructor(namespace: String, path: String): this(Identifier(namespace, path))
}
