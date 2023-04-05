package com.cs328.myrmi.transport

/**
 * providing channel for communication.
 */
interface Endpoint {
    val channel: Channel
    fun exportObject(obj: Target)
}