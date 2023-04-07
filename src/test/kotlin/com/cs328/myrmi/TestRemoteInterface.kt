package com.cs328.myrmi

interface TestRemoteInterface: Remote {
    fun square(x: Int): Int
    fun add(x: Int, y: Int): Int

    fun getValue(): Int
    fun testExp()

    fun print(str: String)

    fun increase(): Int
}