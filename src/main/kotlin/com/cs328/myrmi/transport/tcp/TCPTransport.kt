package com.cs328.myrmi.transport.tcp

import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.transport.*
import com.cs328.myrmi.transport.Target
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class TCPTransport(private var endpoint: TCPEndpoint) : Transport() {
    private val logger by lazy { RMILogger.of(this::class.java.name) }

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

    private lateinit var server: ServerSocket

    private var exportCount = 0
    override fun exportObject(target: Target) {
        logger.fine("export object $target on ${endpoint.host}:${endpoint.port}")
        listen()
        super.exportObject(target)
        synchronized(this) {
            ++exportCount
        }
    }

    @Synchronized
    override fun onTargetClosed() {
        --exportCount
        if (exportCount == 0) {
            try {
                server.close()
            } catch (e: Exception) {
                logger.info("socket server on ${endpoint.port} throw exception on closing: ${e.message}")
            }
            logger.fine("socket server on ${endpoint.port} closed")
        }
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
                logger.fine("socket server start listening on ${endpoint.port}")
                while (true) {
                    val socket = server.accept()
                    logger.fine("accept connection from ${socket.remoteSocketAddress}")
                    connectionThreadPool.execute {
                        socket.use {
                            val tempName = Thread.currentThread().name
                            Thread.currentThread().name =
                                "RMI TCP Connection(${++connectionCount}) - ${socket.remoteSocketAddress}"
                            socket.acceptIncomeTransmission()
                            Thread.currentThread().name = tempName
                        }
                    }
                }
            }
        }
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
                logger.info("unknown RMI request from ${this.remoteSocketAddress}")
                return
            }

            logger.info("accept RMI request from ${this.remoteSocketAddress}")
            val bufOutput = BufferedOutputStream(this.getOutputStream())
            val output = DataOutputStream(bufOutput)

            val remoteConn = TCPConnection(TCPChannel(
                TCPEndpoint(this.inetAddress.toString(), this.port)), bufInput, bufOutput)

            //read protocol type
            when (input.read()) {
                TransportConstants.SINGLE_OP_PROTOCOL -> {
                    logger.fine("accept single op protocol from ${this.remoteSocketAddress}")
                    remoteConn.handleMessage(false)
                }
                TransportConstants.STREAM_PROTOCOL -> {
                    logger.fine("accept stream protocol from ${this.remoteSocketAddress}")
                    output.write(TransportConstants.PROTOCOL_ACK)
                    output.flush()
                    remoteConn.handleMessage(true)
                }
                TransportConstants.MULTIPLEX_PROTOCOL -> {
                    //not supported
                    logger.info("unsupported protocol type from ${this.remoteSocketAddress}")
                    output.write(TransportConstants.PROTOCOL_NACK)
                    output.flush()
                }
                else -> {
                    //unknown protocol
                    logger.info("unknown protocol type from ${this.remoteSocketAddress}")
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
            val ep = this.channel.endpoint as TCPEndpoint
            //read operation
            when (this.inputStream.read()) {
                TransportConstants.CALL -> {
                    logger.fine("accept call from ${ep.host}:${ep.port}")
                    val call = StreamRemoteCall(this)
                    if (!serviceCall(call)) {
                        //release conn if service failed
                        logger.warning("service call failed from ${ep.host}:${ep.port}, connection will be closed")
                        return
                    }
                }
                TransportConstants.PING -> {
                    logger.fine("accept ping from ${ep.host}:${ep.port}")
                    //ping action
                    this.outputStream.write(TransportConstants.PING_ACK)
                    this.outputStream.flush()
                    this.releaseOutputStream()
                }
                TransportConstants.DGC_ACK -> {
                    logger.warning("accept DGC ack from ${ep.host}:${ep.port}, but DGC not yet implemented")
                }
                else -> {
                    logger.info("unknown operation from ${ep.host}:${ep.port}, connection will be closed")
                    //unknown operation or no more operation
                    return
                }
            }
        } while (persistent)
    }
}

