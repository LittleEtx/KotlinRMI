package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.transport.LiveRef
import com.cs328.myrmi.transport.tcp.TCPEndpoint
import java.util.logging.ConsoleHandler
import java.util.logging.Level

interface TestRemoteClass : Remote {
    fun square(x: Int): Int
    fun testExp()
}

fun main() {
    val logger = RMILogger.of(UnicastRef::class.java.name)
    logger.level = Level.ALL
    val handler = ConsoleHandler()
    handler.level = Level.ALL
    logger.addHandler(handler)
    logger.fine("test UnicastRef")

    val liveRef = LiveRef(ObjID(114514), TCPEndpoint("localhost", 8080))
    val ref = UnicastServerRef(liveRef)
    val method = TestRemoteClass::class.java.declaredMethods[0]
    println(method)
    println(ref.invoke(method, arrayOf(3)))
    println(ref.invoke(method, arrayOf(5)))
    val method1 = TestRemoteClass::class.java.declaredMethods[1]
    println(method1)
    println(ref.invoke(method, emptyArray()))
}