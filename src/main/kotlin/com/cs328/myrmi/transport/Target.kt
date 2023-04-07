package com.cs328.myrmi.transport

import com.cs328.myrmi.Remote
import com.cs328.myrmi.server.Dispatcher
import com.cs328.myrmi.server.ObjID
import java.lang.ref.WeakReference

class Target private constructor(
    val weakRef: WeakReference<Remote>,
    val id: ObjID,
    val stub: Remote, //the proxy for this obj
    val dispatcher: Dispatcher,
) {
    private var strongRef: Remote? = null
    constructor(obj: Remote, id: ObjID, stub: Remote, dispatcher: Dispatcher, permanent: Boolean) :
            this(WeakReference(obj), id, stub, dispatcher) {
        if (permanent) {
            strongRef = obj
        }
    }

    lateinit var exportedTransport: Transport

    fun markRemoved() {
        strongRef = null
        if (this::exportedTransport.isInitialized) {
            exportedTransport.onTargetClosed()
        }
    }

}