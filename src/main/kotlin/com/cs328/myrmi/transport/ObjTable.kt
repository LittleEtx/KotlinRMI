package com.cs328.myrmi.transport

import com.cs328.myrmi.Remote
import com.cs328.myrmi.exception.ExportException
import com.cs328.myrmi.exception.NoSuchObjectException
import com.cs328.myrmi.server.ObjID
import java.lang.ref.WeakReference

/** the class that records all exported object */
class ObjTable private constructor() {
    companion object {
        private val tableLock = Any()

        private val objTable = HashMap<ObjectEndpoint, Target>()
        private val implTable = HashMap<WeakReference<Remote>, Target>()

        fun putTarget(target: Target) {
            synchronized(tableLock) {
                val oe = ObjectEndpoint(target.id, target.exportedTransport)
                if (objTable.containsKey(oe)) {
                    throw ExportException("internal error: id ${target.id} already exists")
                }
                if (implTable.containsKey(target.weakRef)) {
                    throw ExportException("The object is already exported")
                }
                objTable[oe] = target
            }
        }

        fun getTarget(objEndpoint: ObjectEndpoint): Target? {
            synchronized(tableLock) {
                return objTable[objEndpoint]
            }
        }

        fun getTarget(impl: Remote): Target? {
            synchronized(tableLock) {
                return implTable[WeakReference(impl)]
            }
        }

        fun removeTarget(obj: Remote) {
            synchronized(tableLock) {
                if (!implTable.containsKey(WeakReference(obj))) {
                    throw NoSuchObjectException("object not exported")
                }
                val target = implTable[WeakReference(obj)]!!
                objTable.remove(ObjectEndpoint(target.id, target.exportedTransport))
                implTable.remove(target.weakRef)
                target.markRemoved()
            }
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