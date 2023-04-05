package com.cs328.myrmi.transport.tcp

import com.cs328.myrmi.transport.Channel
import com.cs328.myrmi.transport.Endpoint
import com.cs328.myrmi.transport.Target
import java.net.ServerSocket
import java.net.Socket

class TCPEndpoint(
    host: String,
    port: Int,
    override val channel: Channel
) : Endpoint {

    override fun exportObject(obj: Target) {
        TODO("Not yet implemented")

    }

    companion object {
        fun getLocalEndPoint(port: Int): TCPEndpoint {
            TODO("Not yet implemented")
        }
    }

    fun newSocket(): Socket {
        TODO("Not yet implemented")
    }

    fun newServerSocket(): ServerSocket {
        TODO("Not yet implemented")
    }
}