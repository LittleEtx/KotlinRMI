package com.cs328.myrmi.transport

import com.cs328.myrmi.Remote
import com.cs328.myrmi.server.Dispatcher
import com.cs328.myrmi.server.ObjID

class Target private constructor(
    val weakRef: WeakRef<Remote>,
    val id: ObjID,
    val stub: Remote, //the proxy for this obj
    private var dispatch: Dispatcher?
) {
    val dispatcher get() = dispatch
    constructor(obj: Remote, id: ObjID, stub: Remote, dispatcher: Dispatcher, permanent: Boolean) :
            this(WeakRef(obj), id, stub, dispatcher) {
        if (permanent) {
            weakRef.pin()
        }
    }

    lateinit var exportedTransport: Transport

    fun markRemoved(force: Boolean) {
        weakRef.unpin()
        if (force) dispatch = null
        if (this::exportedTransport.isInitialized) {
            exportedTransport.onTargetClosed()
        }
    }

}