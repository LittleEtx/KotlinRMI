package com.cs328.myrmi.transport.tcp

import com.cs328.myrmi.exception.ConnectIOException
import com.cs328.myrmi.transport.Channel
import com.cs328.myrmi.transport.Connection
import com.cs328.myrmi.transport.Endpoint
import com.cs328.myrmi.transport.TransportConstants
import java.io.DataOutputStream
import java.io.IOException

class TCPChannel(
    private val ep: TCPEndpoint
) : Channel {

    private var reuseConn = ArrayDeque<TCPConnection>()
    override val endpoint: Endpoint
        get() = ep

    /**
     * Create a new connection to the endpoint.
     * This method will write magic number and version to the output stream,
     * and check if protocol is supported.
     */
    override fun newConnection(): Connection {
        //check if exist reusable connection
        while (reuseConn.isNotEmpty()) {
            val tempConn = reuseConn.removeFirst()
            if (!tempConn.isDead()) {
                return tempConn
            }
        }
        return createConnection()
    }

    override fun free(conn: Connection, reuse: Boolean) {
        if (reuse && conn.isReusable()) {
            reuseConn.addLast(conn as TCPConnection)
        } else {
            conn.close()
        }
    }

    private fun createConnection(): TCPConnection {
        val conn = TCPConnection(this, ep.newSocket())
        try {
            conn.writeTransportHeader()
            conn.writeProtocolHeader()
        } catch (e: IOException) {
            conn.close()
            throw e
        }
        return conn
    }

    private fun Connection.writeTransportHeader() {
        try {
            val out = DataOutputStream(this.outputStream)
            out.writeInt(TransportConstants.MAGIC)
            out.writeByte(TransportConstants.VERSION)
            out.flush()
        } catch (e: IOException) {
            throw ConnectIOException("Failed to write RMI transport header", e)
        }
    }

    private fun Connection.writeProtocolHeader() {
        try {
            val out = DataOutputStream(this.outputStream)
            if (!this.isReusable()) {
                out.writeByte(TransportConstants.SINGLE_OP_PROTOCOL)
                out.flush()
            } else {
                out.writeByte(TransportConstants.STREAM_PROTOCOL)
                out.flush()
                //wait for response
                val rep = this.inputStream.read()
                if (rep!= TransportConstants.PROTOCOL_ACK) {
                    throw ConnectIOException(
                        if (rep == TransportConstants.PROTOCOL_NACK)
                        "Protocol not supported"
                        else "Failed to write RMI header")
                }
            }
        } catch (e: IOException) {
            throw ConnectIOException("Failed to write RMI header", e)
        }
    }
}