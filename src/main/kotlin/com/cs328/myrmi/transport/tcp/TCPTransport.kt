package com.cs328.myrmi.transport.tcp

import com.cs328.myrmi.transport.*
import com.cs328.myrmi.transport.Target
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class TCPTransport(var endpoint: TCPEndpoint) : Transport() {
    companion object {
        private const val maxConnectionThreads = Int.MAX_VALUE
        private const val maxConnectionIdleTime = 60000L  //default 60s

        /** thread pool for handling connection */
        private val connectionThreadPool = ThreadPoolExecutor(
            0,
            maxConnectionThreads,
            maxConnectionIdleTime,
            TimeUnit.MICROSECONDS,
            SynchronousQueue()
        ) { r ->
            val thread = Thread(r)
            thread.isDaemon = true
            thread.name = "RMI TCP Connection(idle)"
            thread
        }
    }


    private var exportCount = 0
    private lateinit var server: ServerSocket
    override fun exportObject(obj: Target) {
        synchronized(this) {
            listen()
            ++exportCount
        }
        super.exportObject(obj)
    }

    private var connectionCount = 0

    /**
     * create thread for socket to listen
     */
    private fun listen() {
        //only init listen thread once
        if (::server.isInitialized) return

        server = endpoint.newServerSocket()
        val thread = Thread {
            server.use {
                while (true) {
                    val socket = server.accept()
                    connectionThreadPool.execute {
                        socket.use {
                            val tempName = Thread.currentThread().name
                            Thread.currentThread().name =
                                "RMI TCP Connection(${++connectionCount}) - ${it.remoteSocketAddress}"
                            it.acceptIncomeTransmission()
                            Thread.currentThread().name = tempName
                        }
                    }
                }
            }
        }
        thread.isDaemon = true
        thread.name = "RMI TCP Accept - ${endpoint.port}"
        thread.start()
    }

    private fun Socket.acceptIncomeTransmission() {
        try {
            //if possible, set to no delay
            this.tcpNoDelay = true
        } catch (ignore: Exception) {
        }

        try {
            val bufInput = BufferedInputStream(this.getInputStream())
            val input = DataInputStream(bufInput)
            val magic = input.readInt()
            val version = input.readShort()

            //unsupported RMI request
            if (magic != TransportConstants.MAGIC ||
                version.toInt() != TransportConstants.VERSION
            ) {
                return
            }

            val bufOutput = BufferedOutputStream(this.getOutputStream())
            val output = DataOutputStream(bufOutput)

            val remoteConn = TCPConnection(TCPChannel(
                TCPEndpoint(this.remoteSocketAddress.toString(), this.port)), bufInput, bufOutput)

            //read protocol type
            when (input.read()) {
                TransportConstants.SINGLE_OP_PROTOCOL -> {
                    remoteConn.handleMessage(false)
                }
                TransportConstants.STREAM_PROTOCOL -> {
                    remoteConn.handleMessage(true)
                }
                else -> {
                    //not supported
                    output.write(TransportConstants.PROTOCOL_NACK)
                    output.flush()
                }
            }
        } catch (ignore: IOException) {
            //ignore any socket err and destroy the socket
        }
    }

    /**
     * handle the method call. Connection is the connection to the client
     * @param persistent if the connection is persistent, i.e. can handle many requests
     */
    private fun Connection.handleMessage(persistent: Boolean) {
        do {
            //read operation
            when (this.inputStream.read()) {
                TransportConstants.CALL -> {
                    val call = StreamRemoteCall(this)
                    if (serviceCall(call)) return  //release conn if service failed
                }
                TransportConstants.PING -> {
                    //ping action
                    this.outputStream.write(TransportConstants.PING_ACK)
                    this.outputStream.flush()
                    this.releaseOutputStream()
                }
                TransportConstants.DGC_ACK -> {
                    TODO("DGC not yet implemented")
                }
                else -> {
                    //unknown operation or no more operation
                    return
                }
            }
        } while (persistent)
    }
}

