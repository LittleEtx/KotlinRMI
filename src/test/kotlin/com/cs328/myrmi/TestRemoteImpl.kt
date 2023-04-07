package com.cs328.myrmi

import kotlin.random.Random

interface AnotherTestInterface: Remote {
    fun square(x: Int): Int
    fun add(x: Int, y: Int): Int

    fun getValue(): Int
    fun testExp()

    fun print(str: String)
}

class TestRemoteImpl: AnotherTestInterface {
    override fun square(x: Int): Int {
        return x * x
    }

    override fun add(x: Int, y: Int): Int {
        return x + y
    }

    override fun getValue(): Int {
        val num = Random.nextInt()
        println("Random number: $num")
        return num
    }

    override fun testExp() {
        throw RuntimeException("Test exception thrown from the serve")
    }

    override fun print(str: String) {
        println(str)
    }
}