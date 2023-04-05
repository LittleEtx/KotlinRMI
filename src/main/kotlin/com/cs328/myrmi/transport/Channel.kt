package com.cs328.myrmi.transport

/**
 * A channel provides connection to a certain endpoint.
 */
interface Channel {
    val endpoint: Endpoint
    fun newConnection(): Connection
    fun free(conn: Connection, reuse: Boolean)
}