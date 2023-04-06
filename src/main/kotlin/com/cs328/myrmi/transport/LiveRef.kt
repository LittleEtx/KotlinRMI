package com.cs328.myrmi.transport

import com.cs328.myrmi.server.ObjID
import com.cs328.myrmi.transport.tcp.TCPEndpoint

/**
 * Help creating channels to a given endpoint. With Channel connection
 * can be created for transmission.
 */
class LiveRef(val id: ObjID, val endpoint: Endpoint) {
    fun exportObject(target: Target) {
        endpoint.exportObject(target)
    }

    val channel: Channel by lazy { endpoint.channel }

    /** get a live ref basing on the local endpoint */
    constructor(id: ObjID, port: Int) : this(id, TCPEndpoint.getLocalEndPoint(port))
}