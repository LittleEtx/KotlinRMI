package com.cs328.myrmi.transport

import com.cs328.myrmi.exception.MarshalException
import com.cs328.myrmi.exception.NoSuchObjectException
import com.cs328.myrmi.server.ObjID
import com.cs328.myrmi.server.RemoteCall
import java.io.IOException

/**
 * The class that deals with remote method invocation.
 */
abstract class Transport {

    /**
     * Record the exported remote obj
     */
    open fun exportObject(target: Target) {
        target.exportedTransport = this
        ObjTable.putTarget(target)
    }

    /**
     * actually executes the method called by remote host
     */
    fun serviceCall(call: RemoteCall): Boolean {
        //read obj id
        val objID: ObjID
        try {
            objID = call.inputStream.readObject() as ObjID
        } catch (e: Exception) {
            throw MarshalException("failed to read object id", e)
        }
        val target = ObjTable.getTarget(ObjTable.ObjectEndpoint(objID, this))
        if (target == null || target.weakRef.get() == null) {
            throw NoSuchObjectException("no such implement in the table")
        }
        try {
            target.dispatcher.dispatch(target.weakRef.get()!!, call)
        } catch (e: IOException) {
            //something goes wrong when dealing with remote call
            return false
        }
        return true
    }

    /** the function will be called when the target is closed for export */
    abstract fun onTargetClosed()
}