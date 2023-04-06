package com.cs328.myrmi.transport.tcp

import com.cs328.myrmi.transport.Endpoint
import com.cs328.myrmi.transport.Target
import java.net.ServerSocket
import java.net.Socket

class TCPEndpoint private constructor(
    val host: String,
    val port: Int,
    private val isLocal: Boolean
): Endpoint {

    /** create a remote end point */
    constructor(host: String, port: Int): this(host, port, false)

    private val transports by lazy { TCPTransport(this) }

    override val channel by lazy { TCPChannel(this) }

    override fun exportObject(obj: Target) {
        if (!isLocal)
            throw IllegalStateException("Cannot export object for non-local endpoint")
        transports.exportObject(obj)
    }

    companion object {
        /**
         * create a local endpoint on a certain port.
         * this method will ensure that each port has only one transport for receiving msg
         */
        fun getLocalEndPoint(port: Int): TCPEndpoint {
            synchronized(localEndpoints) {
                return localEndpoints.getOrPut(port) { TCPEndpoint("localhost", port, true) }
            }
        }

        /** map from port to transport */
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
        return ServerSocket(port)
    }
}