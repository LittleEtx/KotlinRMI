package com.cs328.myrmi.server

import java.io.ObjectInput
import java.io.ObjectOutput

/**
 * Provide stream methods for writing parameters and reading return values.
 * Also deals with the exception return from the remote
 */
interface RemoteCall {
    /** the output stream for writing parameters */
    val outputStream: ObjectOutput
    fun releaseOutputStream()
    /** input stream for both server and client */
    val inputStream: ObjectInput
    fun releaseInputStream()

    /** the result writing stream for server */
    fun getResultStream(success: Boolean): ObjectOutput

    /**
     * execute remote call
     */
    fun executeCall()

    /**
     * finish the remote call
     */
    fun done()

}