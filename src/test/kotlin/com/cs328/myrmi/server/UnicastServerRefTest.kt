package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.transport.LiveRef
import java.util.logging.ConsoleHandler
import java.util.logging.Level

const val id = 114514L

interface AnotherTestClass : Remote {
    fun square(x: Int): Int
    fun testExp()
}

fun main() {
    val logger = RMILogger.parentLogger
    logger.level = Level.ALL
    val handler = ConsoleHandler()
    handler.level = Level.ALL
    logger.addHandler(handler)
    logger.fine("test UnicastServerRef")

    val testObject = object : AnotherTestClass {
        override fun square(x: Int): Int {
            return x * x
        }

        override fun testExp() {
            throw RuntimeException("Exception from the serve")
        }
    }
    val ref = LiveRef(ObjID(id), 8080)
    UnicastServerRef(ref).exportObject(testObject, true)
}