package com.cs328.myrmi.transport

import com.cs328.myrmi.Remote
import com.cs328.myrmi.exception.ExportException
import com.cs328.myrmi.exception.NoSuchObjectException
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.server.ObjID

/** the class that records all exported object */
object ObjTable {
    private val logger = RMILogger.of(ObjTable::class.java.name)
    private val tableLock = Any()

    private val objTable = HashMap<ObjectEndpoint, Target>()
    private val implTable = HashMap<WeakRef<Remote>, Target>()

    fun putTarget(target: Target) {
        logger.fine("put target $target into table, impl: ${target.weakRef.get()}")
        synchronized(tableLock) {
            val oe = ObjectEndpoint(target.id, target.exportedTransport)
            if (objTable.containsKey(oe)) {
                throw ExportException("internal error: id ${target.id} already exists")
            }
            if (implTable.containsKey(target.weakRef)) {
                throw ExportException("The object is already exported")
            }
            implTable[target.weakRef] = target
            objTable[oe] = target
        }
    }

    fun getTarget(objEndpoint: ObjectEndpoint): Target? {
        synchronized(tableLock) {
            return objTable[objEndpoint]
        }
    }

    fun getTarget(impl: Remote): Target? {
        logger.fine("get target for impl $impl")
        synchronized(tableLock) {
            return implTable[WeakRef(impl)]
        }
    }

    fun removeTarget(obj: Remote, force: Boolean) {
        synchronized(tableLock) {
            if (!implTable.containsKey(WeakRef(obj))) {
                throw NoSuchObjectException("object not exported")
            }
            val target = implTable[WeakRef(obj)]!!
            objTable.remove(ObjectEndpoint(target.id, target.exportedTransport))
            implTable.remove(target.weakRef)
            target.markRemoved(force)
        }
    }


    class ObjectEndpoint(val objID: ObjID, val transport: Transport) {
        override fun equals(other: Any?): Boolean {
            if (other == null || other !is ObjectEndpoint) {
                return false
            }
            return objID == other.objID && transport == other.transport
        }

        override fun hashCode(): Int {
            return objID.hashCode() + transport.hashCode()
        }
    }
}