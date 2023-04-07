package com.cs328.myrmi.registry

import com.cs328.myrmi.TestRemoteInterface
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.server.UnicastRef
import java.util.logging.ConsoleHandler
import java.util.logging.Level

fun main() {
    val logger = RMILogger.of(UnicastRef::class.java.name)
    logger.level = Level.ALL
    val handler = ConsoleHandler()
    handler.level = Level.ALL
    logger.addHandler(handler)

    val registry = LocateRegistry.getRegistry("localhost", 8080)
    val test = registry.lookup("test") as TestRemoteInterface
    var test2 = registry.lookup("test2") as TestRemoteInterface
    println("increase number in remote to ${test2.increase()}")
    test2 = registry.lookup("test2") as TestRemoteInterface
    println("increase number in remote to ${test2.increase()}")
    test.print("Hello World!")
    println("square of 3 is ${test.square(3)}")
    println("adding up 14 and 9 is ${test.add(14, 9)}")
    try {
        test.testExp()
    } catch (e: Exception) {
        println("Exception caught:")
        e.printStackTrace()
    }

    println("increase number in remote to ${test.increase()}")
    println("value is ${test.getValue()}")
}