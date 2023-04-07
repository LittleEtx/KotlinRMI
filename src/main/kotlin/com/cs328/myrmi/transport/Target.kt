package com.cs328.myrmi.transport

import com.cs328.myrmi.Remote
import com.cs328.myrmi.server.Dispatcher
import com.cs328.myrmi.server.ObjID

class Target private constructor(
    val weakRef: WeakRef<Remote>,
    val id: ObjID,
    val stub: Remote, //the proxy for this obj
    val dispatcher: Dispatcher,
) {
    constructor(obj: Remote, id: ObjID, stub: Remote, dispatcher: Dispatcher, permanent: Boolean) :
            this(WeakRef(obj), id, stub, dispatcher) {
        if (permanent) {
            weakRef.pin()
        }
    }

    lateinit var exportedTransport: Transport

    fun markRemoved() {
        weakRef.unpin()
        if (this::exportedTransport.isInitialized) {
            exportedTransport.onTargetClosed()
        }
    }

}