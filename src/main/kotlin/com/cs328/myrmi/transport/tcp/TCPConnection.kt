package com.cs328.myrmi.transport.tcp

import com.cs328.myrmi.transport.Channel
import com.cs328.myrmi.transport.Connection
import com.cs328.myrmi.transport.TransportConstants
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class TCPConnection(
    override val channel: Channel,
    override val inputStream: InputStream,
    override val outputStream: OutputStream
) : Connection {
    private var socket: Socket? = null
    constructor(channel: Channel, socket: Socket) : this(
        channel,
        BufferedInputStream(socket.getInputStream()),
        BufferedOutputStream(socket.getOutputStream())
    ) {
        this.socket = socket
    }
    override fun releaseInputStream() {
        //do nothing here
    }

    override fun releaseOutputStream() {
        outputStream.flush()
    }

    override fun close() {
        try {
            if (socket != null) {
                socket!!.close()
            } else {
                inputStream.close()
                outputStream.close()
            }
        } catch (ignore: IOException) {
        }
    }

    override fun isReusable(): Boolean {
        return true
    }

    var roundTrip = -1
        private set
    var lastPing = -1L
        private set

    /**
     * ping the server to see if connection is dead
     */
    fun isDead(): Boolean {
        val start = System.currentTimeMillis()
        //if currently pinged
        if (start - lastPing < roundTrip) {
            return false
        }

        val ans: Int
        try {
            outputStream.write(TransportConstants.PING)
            outputStream.flush()
            ans = inputStream.read()
        } catch (e: IOException) {
            return true
        }
        if (ans == TransportConstants.PING_ACK) {
            lastPing = System.currentTimeMillis()
            roundTrip = (lastPing - start).toInt()
            return false
        }
        // the server didn't respond with a PING_ACK, or it is dead
        return true
    }
}