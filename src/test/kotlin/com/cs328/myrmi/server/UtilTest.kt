package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.TestRemoteInterface
import com.cs328.myrmi.transport.LiveRef
import com.cs328.myrmi.transport.tcp.TCPEndpoint

interface RemoteInt: Remote
open class A: RemoteInt
class B: A()

fun main() {
    println(Remote::class.java.isAssignableFrom(B::class.java))
    println(Util.getRemoteInterfaces(B::class.java).joinToString(" "))

    val liveRef = LiveRef(ObjID(114514), TCPEndpoint("localhost", 8080))
    val ref = UnicastRef(liveRef)
    val test = Util.createProxy(TestRemoteInterface::class.java, ref) as TestRemoteInterface

    println(test.square(3))
    println(test.square(5))
    println(test.add(3, 8))
    test.print("Hello World!")
}