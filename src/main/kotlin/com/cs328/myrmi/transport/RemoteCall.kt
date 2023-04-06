package com.cs328.myrmi.transport

import com.cs328.myrmi.Remote
import java.io.ObjectInput
import java.io.ObjectOutput
import java.lang.reflect.Method

/**
 * Provide stream methods for writing parameters and reading return values.
 * Also deals with the exception return from the remote
 */
interface RemoteCall {
    /** the output stream for writing parameters */
    val outputSteam: ObjectOutput
    fun releaseOutputStream()
    /** input stream for both server and client */
    val inputStream: ObjectInput
    fun releaseInputStream()

    /** the result writing stream for server */
    val resultStream: ObjectOutput

    /**
     * execute remote call
     */
    fun executeCall(obj: Remote, method: Method, params: Array<Any>): Any?

    /**
     * finish the remote call
     */
    fun done()

}