package com.cs328.myrmi.transport

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

/**
 * This class is used to create a weak reference to a remote object.
 * It is used to store the remote object in the object table
 */
class WeakRef<T>: WeakReference<T> {
    constructor(obj: T): super(obj)
    constructor(obj: T, queue: ReferenceQueue<in T>): super(obj, queue)
    private var hash by Delegates.notNull<Int>()
    init {
        hash = System.identityHashCode(get())
    }

    private var strongRef: T? = null
    fun pin() {
        strongRef = get()
    }
    fun unpin() {
        strongRef = null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WeakRef<*>

        return get() == other.get()
    }

    override fun hashCode(): Int {
        return hash
    }
}