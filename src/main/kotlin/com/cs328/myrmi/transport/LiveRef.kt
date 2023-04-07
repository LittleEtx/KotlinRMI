package com.cs328.myrmi.transport

import com.cs328.myrmi.server.ObjID
import com.cs328.myrmi.transport.tcp.TCPEndpoint

/**
 * This class helps to create channels to a given endpoint. With Channel connection
 * can be created for transmission.
 */
class LiveRef(val id: ObjID, val endpoint: Endpoint) {

    val channel: Channel by lazy { endpoint.channel }

    /** get a live ref basing on the local endpoint */
    constructor(id: ObjID, port: Int) : this(id, TCPEndpoint.getLocalEndPoint(port))

    fun exportObject(target: Target) {
        endpoint.exportObject(target)
    }

    fun remoteEquals(obj: Any): Boolean {
        if (obj !is LiveRef) return false
        endpoint as TCPEndpoint
        obj.endpoint as TCPEndpoint
        return endpoint.host == obj.endpoint.host
                && endpoint.port == obj.endpoint.port
                && id == obj.id
    }

    override fun toString(): String {
        endpoint as TCPEndpoint
        return "[endpoint:$endpoint(${if (endpoint.isLocal) "local" else "remote"}), objID:$id]"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LiveRef

        if (id != other.id) return false
        return endpoint == other.endpoint
    }
}