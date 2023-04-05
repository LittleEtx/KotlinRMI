package com.cs328.myrmi.transport

import java.io.InputStream
import java.io.OutputStream

/**
 * Proving input and output streams for communication.
 */
interface Connection {
    val inputStream: InputStream
    val outputStream: OutputStream
    val channel: Channel
    fun releaseInputStream()
    fun releaseOutputStream()
    fun close()
    fun isReusable(): Boolean
}