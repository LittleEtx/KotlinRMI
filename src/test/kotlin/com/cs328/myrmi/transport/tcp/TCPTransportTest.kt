package com.cs328.myrmi.transport.tcp
import com.cs328.myrmi.Remote
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.server.Dispatcher
import com.cs328.myrmi.server.ObjID
import com.cs328.myrmi.server.RemoteCall
import com.cs328.myrmi.transport.Target
import java.util.logging.ConsoleHandler
import java.util.logging.Level

fun main() {
    val logger = RMILogger.of(TCPTransport::class.java.name)
    logger.level = Level.ALL
    val handler = ConsoleHandler()
    handler.level = Level.ALL
    logger.addHandler(handler)
    logger.fine("test TCPTransport")

    val obj = object : Remote {}
    val dispatcher = object: Dispatcher {
        override fun dispatch(obj: Remote, call: RemoteCall) {
            //do nothing
        }
    }

    val transport = TCPTransport(TCPEndpoint.getLocalEndPoint(8080))
    transport.exportObject(Target(obj, ObjID.new(), dispatcher, true))
    var conn = TCPEndpoint.getLocalEndPoint(8080).channel.newConnection()
    conn as TCPConnection
    println(conn.isDead())
    TCPEndpoint.getLocalEndPoint(8080).channel.free(conn, true)
    conn = TCPEndpoint.getLocalEndPoint(8080).channel.newConnection()
    conn as TCPConnection
    println(conn.isDead())
}