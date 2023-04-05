package com.cs328.myrmi.transport.tcp

import com.cs328.myrmi.transport.Channel
import com.cs328.myrmi.transport.Endpoint
import com.cs328.myrmi.transport.Target
import java.net.ServerSocket
import java.net.Socket

class TCPEndpoint private constructor(
    private val host: String,
    private val port: Int,
    private val isLocal: Boolean
): Endpoint {

    /** create a remote end point */
    constructor(host: String, port: Int): this(host, port, false)

    init {
        if (isLocal) {
            synchronized(transports) {
                transports.getOrPut(port) { TCPTransport(this) }
                    .endpoint = this
            }
        }
    }

    override val channel = TCPChannel(this)

    override fun exportObject(obj: Target) {
        if (!isLocal)
            throw IllegalStateException("Cannot export object for non-local endpoint")
        transports[port]!!.exportObject(obj)
    }

    companion object {
        /**
         * create a local endpoint on a certain port.
         * this method will ensure that each port has only one transport for receiving msg
         */
        fun getLocalEndPoint(port: Int): TCPEndpoint {
            return TCPEndpoint("localhost", port, true)
        }

        /** map from port to transport */
        private val transports = mutableMapOf<Int, TCPTransport>()
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