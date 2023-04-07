package com.cs328.myrmi.registry

import com.cs328.myrmi.TestRemoteInterface

fun main() {
    val registry = LocateRegistry.getRegistry("localhost", 8080)
    val test = registry.lookup("test") as TestRemoteInterface
    test.print("Hello World!")
    println("square of 3 is ${test.square(3)}")
    println("adding up 14 and 9 is ${test.add(14, 9)}")
    try {
        test.testExp()
    } catch (e: Exception) {
        println("Exception caught: ${e.message}")
    }
    println("random number is ${test.getValue()}")
}