package com.stultorum.quiltmc.blockAttributes.locks

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.StampedLock

typealias Stamp = Long

class AttributeLocker {
    private val globalLock: StampedLock = StampedLock()
    private val activeLocks = ConcurrentHashMap<IIdentifierPattern, StampedLock>()
    // Since requesting a read lock is nonexclusive, and requesting a set lock is, it's the perfect time for a stamped lock, albeit a nonconventional one.
    private var lockLock = StampedLock()
    
    fun readLock(newPattern: IIdentifierPattern): Stamp {
        return lock(newPattern, { lock -> lock.readLockInterruptibly() }, { lock, stamp -> lock.unlockRead(stamp) })
    }
    
    fun verifyOrReadLock(newPattern: IIdentifierPattern, existingStamp: Stamp): Stamp? {
        if (hasLock(newPattern, existingStamp)) return null
        return readLock(newPattern)
    }
    
    fun writeLock(newPattern: IIdentifierPattern): Stamp {
        return lock(newPattern, { lock -> lock.writeLockInterruptibly() }, { lock, stamp -> lock.unlockWrite(stamp) })
    }
    
    fun verifyOrWriteLock(newPattern: IIdentifierPattern, existingStamp: Stamp): Stamp? {
        if (hasLock(newPattern, existingStamp)) return null
        return writeLock(newPattern)
    }
    
    fun unlock(pattern: IIdentifierPattern, stamp: Stamp) {
        if (pattern is GlobalPattern) return globalLock.unlock(stamp)
        return activeLocks[pattern]!!.unlock(stamp)
    }
    
    private fun hasLock(pattern: IIdentifierPattern, existingStamp: Stamp): Boolean {
        if (pattern is GlobalPattern) return globalLock.validate(existingStamp)
        return activeLocks[pattern]?.validate(existingStamp) ?: false
    }
    
    private fun lock(newPattern: IIdentifierPattern, lock: (StampedLock) -> Stamp, unlock: (StampedLock, Stamp) -> Unit): Stamp {
        if (newPattern is GlobalPattern) {
            val stamp = lock(lockLock)
            val handles = activeLocks.map { Pair(it.value, lock(it.value)) }
            try {
                return lock(globalLock)
            } finally {
                handles.forEach { unlock(it.first, it.second) }
                unlock(lockLock, stamp)
            }
        }

        val tempLocks = ArrayList<Pair<StampedLock, Stamp>>()
        var mostExactPattern: IIdentifierPattern = GlobalPattern()
        val stamp = lock(lockLock)

        try {
            for ((pat, lok) in activeLocks) {
                if (pat.isIdenticalTo(newPattern)) return lock(lok)
                if (newPattern.isSubsetOf(pat) || mostExactPattern.isSubsetOf(pat)) {
                    if (pat.isSubsetOf(mostExactPattern)) {
                        mostExactPattern = pat
                    }
                    tempLocks.add(Pair(lok, lock(lok)))
                }
            }
            val lok = StampedLock()
            activeLocks[newPattern] = lok
            return lock(lok)
        } finally {
            for ((lok, samp) in tempLocks) { unlock(lok, samp) }
            unlock(lockLock, stamp)
            disposeUnusedLocks()
        }
    }
    
    fun disposeUnusedLocks() {
        val iter = activeLocks.iterator()
        while (iter.hasNext()) {
            val (_, lock) = iter.next()
            if (!(lock.isReadLocked || lock.isWriteLocked)) iter.remove()
        }
    }
}
