package com.cs328.myrmi.transport.tcp

import com.cs328.myrmi.transport.Channel
import com.cs328.myrmi.transport.Endpoint
import com.cs328.myrmi.transport.Target
import java.io.Serializable
import java.net.ServerSocket
import java.net.Socket

/**
 * Endpoint for TCP transport, representing a host
 */
class TCPEndpoint private constructor(
    val host: String,
    private var listenPort: Int,
    val isLocal: Boolean
): Endpoint, Serializable {
    val port get() = listenPort

    /** create a remote end point */
    constructor(host: String, port: Int): this(host, port, false)

    @delegate:Transient
    private val transports by lazy { TCPTransport(this) }

    @Transient
    var epChannel: Channel? = null
    override val channel: Channel get() {
        if (epChannel == null) {
            epChannel = TCPChannel(this)
        }
        return epChannel as Channel
    }

    override fun exportObject(obj: Target) {
        if (!isLocal)
            throw IllegalStateException("Cannot export object for non-local endpoint")
        transports.exportObject(obj)
    }

    companion object {
        private const val serialVersionUID = -1578066115088043375L
        /**
         * create a local endpoint on a certain port.
         * this method will ensure that each port has only one transport for receiving msg
         */
        fun getLocalEndPoint(port: Int): TCPEndpoint {
            synchronized(localEndpoints) {
                return localEndpoints.getOrPut(port) { TCPEndpoint("localhost", port, true) }
            }
        }

        /** map from port to endpoints, ensuring no multiply local endpoints on a port are created */
        private val localEndpoints = mutableMapOf<Int, TCPEndpoint>()
    }

    fun newSocket(): Socket {
        val socket = Socket(host, port)
        try {
            socket.tcpNoDelay = true
            socket.keepAlive = true
        } catch (ignore: Exception) {
        }
        return socket
    }

    fun newServerSocket(): ServerSocket {
        if (!isLocal)
            throw IllegalStateException("Cannot create server socket for non-local endpoint")
        val server = ServerSocket(port)
        if (listenPort == 0) {
            //replace with new call
            listenPort = server.localPort
            localEndpoints[listenPort] = this
            localEndpoints.remove(0)
        }
        return server
    }

    override fun toString(): String {
        return "[$host:$port]"
    }
}