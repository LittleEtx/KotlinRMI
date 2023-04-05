package com.cs328.myrmi.transport

/**
 * The class that deals with remote method invocation.
 */
open class Transport {

    /**
     * Record the exported remote obj
     */
    open fun exportObject(obj: Target) {
        //TODO
    }

    /**
     * actually executes the method called by remote host
     */
    fun serviceCall(call: RemoteCall): Boolean {
        //TODO
        return true
    }
}