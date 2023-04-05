package com.cs328.myrmi.transport

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

/**
 * Proving input and output streams for communication.
 */
interface Connection : Closeable {
    val inputStream: InputStream
    val outputStream: OutputStream
    val channel: Channel
    val isReusable: Boolean
    fun releaseInputStream()
    fun releaseOutputStream()
}