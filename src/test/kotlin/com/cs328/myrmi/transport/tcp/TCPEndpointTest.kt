package com.cs328.myrmi.transport.tcp

fun main() {
    val client = TCPEndpoint("localhost", 8080)
    val server = TCPEndpoint.getLocalEndPoint(8080)
    val serverSocket = server.newServerSocket()
    val clientSocket = client.newSocket()
    clientSocket.getOutputStream().write("hello".toByteArray())
    val bytes = ByteArray(1024)
    val len = serverSocket.accept().getInputStream().read(bytes)
    println(String(bytes, 0, len))
}