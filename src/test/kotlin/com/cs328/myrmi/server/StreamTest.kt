package com.cs328.myrmi.server

import com.cs328.myrmi.transport.StreamRemoteCall
import com.cs328.myrmi.transport.tcp.TCPConnection
import com.cs328.myrmi.transport.tcp.TCPEndpoint
import java.io.InputStream
import java.io.OutputStream

class TestClass {
    fun testMethod(){}
}
fun main() {
    val method = TestClass::class.java.getDeclaredMethod("testMethod")
    val ep = TCPEndpoint("localhost", 8080)

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

    val conn = TCPConnection(ep.channel, input, output)
    val params = emptyArray<Any?>()
    val rc = StreamRemoteCall(conn, ObjID(114514), Util.getMethodHash(method))
    params.forEach { rc.outputStream.writeObject(it) }
    rc.releaseOutputStream()

    println(conn.inputStream.read())
    println(rc.inputStream.readObject())
    println(rc.inputStream.readObject())
    method.parameters.map { readln() }.toTypedArray()
}