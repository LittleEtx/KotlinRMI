package com.cs328.myrmi.registry

import com.cs328.myrmi.TestRemoteInterface
import com.cs328.myrmi.server.MarshalOutputStream
import com.cs328.myrmi.server.ObjID
import com.cs328.myrmi.server.UnicastRef
import com.cs328.myrmi.server.Util
import com.cs328.myrmi.transport.LiveRef
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.OutputStream
import java.lang.reflect.Proxy

fun main() {
    val buffer = ArrayDeque<Byte>()
    val input = object : InputStream() {
        override fun read(): Int {
            return buffer.removeFirst().toInt()
        }
    }
    val output = object : OutputStream() {
        override fun write(b: Int) {
            buffer.addLast(b.toByte())
        }
    }
    val liveRef = LiveRef(ObjID(114514), 8080)
    val proxy = Util.createProxy(TestRemoteInterface::class.java, UnicastRef(liveRef))

    val writer = MarshalOutputStream(output)
    writer.writeObject(proxy)
    val reader = ObjectInputStream(input)
    val obj = reader.readObject() as TestRemoteInterface
    println(Proxy.isProxyClass(obj.javaClass))
    println(obj.square(3))
}