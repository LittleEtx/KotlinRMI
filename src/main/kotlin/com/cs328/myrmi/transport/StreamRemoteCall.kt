package com.cs328.myrmi.transport

import com.cs328.myrmi.exception.ConnectIOException
import com.cs328.myrmi.exception.MarshalException
import com.cs328.myrmi.exception.UnmarshalException
import com.cs328.myrmi.server.ObjID
import com.cs328.myrmi.server.RemoteCall
import java.io.*

class StreamRemoteCall(private val connection: Connection) : RemoteCall {

    /**
     * create StreamRemoteCall for client
     */
    constructor(conn: Connection, id: ObjID, methodHash: Long): this(conn) {
        try {
            val output = conn.outputStream
            output.write(TransportConstants.CALL)
            //now get output stream, this action will write a header to the stream
            outputStream.writeObject(id)
            outputStream.writeObject(methodHash)
        } catch (e: IOException) {
            throw MarshalException("failed to write remote method head", e)
        }
    }

    /** get output stream for client */
    override val outputStream: ObjectOutput by lazy {
        try {
            ObjectOutputStream(connection.outputStream)
        } catch (e: IOException) {
            throw ConnectIOException("failed to create output stream", e)
        }
    }

    override fun releaseOutputStream() {
        connection.releaseOutputStream()
    }

    override val inputStream: ObjectInput by lazy {
        try {
            ObjectInputStream(connection.inputStream)
        } catch (e: IOException) {
            throw ConnectIOException("failed to create input stream", e)
        }
    }

    override fun releaseInputStream() {
        connection.releaseInputStream()
    }

    private var resultStarted = false

    /** get output stream for server, this method will write the header for output */
    override fun getResultStream(success: Boolean): ObjectOutput {
        //ensure only run once
        if (resultStarted) {
            throw StreamCorruptedException("result stream already started")
        }
        resultStarted = true
        try {
            val out = connection.outputStream
            out.write(TransportConstants.RETURN)
            out.write(if (success) TransportConstants.NORMAL_RETURN else TransportConstants.EXCEPTION_RETURN)
        } catch (e: IOException) {
            throw MarshalException("failed to write return code", e)
        }
        //write header to the stream
        return outputStream
    }

    /** read remote call return input from server */
    override fun executeCall() {
        val returnCode: Int
        try {
            val input = connection.inputStream
            //first init input stream, receive header
            val code = input.read()
            if (code != TransportConstants.RETURN) {
                throw UnmarshalException("invalid return code, " +
                        "expect ${TransportConstants.RETURN}, but got $code")
            }
            returnCode = input.read()
        } catch (e: IOException) {
            throw UnmarshalException("error in reading return header", e)
        }

        when (returnCode) {
            TransportConstants.NORMAL_RETURN -> {
                return //let caller handle return value
            }
            TransportConstants.EXCEPTION_RETURN -> {
                //exception return, throw the returned exception
                try {
                    val exception = inputStream.readObject()
                    if (exception !is Exception) throw UnmarshalException("invalid exception return")
                    handleException(exception)
                } catch (e: IOException) {
                    throw UnmarshalException("error in reading exception", e)
                } catch (e: ClassNotFoundException) {
                    throw UnmarshalException("error in reading exception", e)
                }
            }
            else -> {
                throw UnmarshalException("invalid return type")
            }
        }

    }

    /** exception throw by the server */
    var serverException: Throwable? = null
        private set

    private fun handleException(exception: Exception) {
        //combine the trace of the exception with the trace of the remote call
        val serverTrace = exception.stackTrace
        val clientTrace = Thread.currentThread().stackTrace
        exception.stackTrace = serverTrace + clientTrace
        serverException = exception
        throw exception
    }

    override fun done() {
        releaseInputStream()
    }
}