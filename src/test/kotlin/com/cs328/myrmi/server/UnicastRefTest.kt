package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.transport.LiveRef
import com.cs328.myrmi.transport.tcp.TCPEndpoint
import java.util.logging.ConsoleHandler
import java.util.logging.Level

interface TestRemoteClass : Remote {
    fun square(x: Int): Int
    fun add(x: Int, y: Int): Int
    fun print(str: String)
    fun getValue(): Int
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
    val ref = UnicastRef(liveRef)

    val method = TestRemoteClass::class.java.getDeclaredMethod("square", Int::class.java)
    println(method)
    println(ref.invoke(method, arrayOf(3)))
    println(ref.invoke(method, arrayOf(5)))

    val multiParaMethod = TestRemoteClass::class.java.getDeclaredMethod("add", Int::class.java, Int::class.java)
    println(multiParaMethod)
    println(ref.invoke(multiParaMethod, arrayOf(3,8)))

    val noReturnMethod = TestRemoteClass::class.java.getDeclaredMethod("print", String::class.java)
    println(noReturnMethod)
    println(ref.invoke(noReturnMethod, arrayOf("Hello World!")))

    val noParaMethod = TestRemoteClass::class.java.getDeclaredMethod("getValue")
    println(noParaMethod)
    println(ref.invoke(noParaMethod, emptyArray()))

    val exceptMethod = TestRemoteClass::class.java.getDeclaredMethod("testExp")
    println(exceptMethod)
    println(ref.invoke(exceptMethod, emptyArray()))
}