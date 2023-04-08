package com.cs328.myrmi

fun main() {
    val test = Naming.lookup("rmi://localhost:8080/test") as TestRemoteInterface
    test.print("Testing remote naming begins")
    println("increase server field to ${test.increase()}")
    println("square of 5 is ${test.square(5)}")
    println("add 5 to 6 is ${test.add(5, 6)}")
    println("value from server is ${test.getValue()}")
    try {
        test.testExp()
    } catch (e: Exception) {
        println("Exception caught from server: ${e.message}")
    }
    test.print("Testing remote naming finished")

}