package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.exception.MarshalException
import com.cs328.myrmi.transport.LiveRef
import com.cs328.myrmi.transport.StreamRemoteCall
import java.io.IOException
import java.lang.reflect.Method

/**
 * stub class in the client
 */
open class UnicastRef(val liveRef: LiveRef) : RemoteRef {

    override fun invoke(obj: Remote, method: Method, params: Array<Any?>, methodHash: Long): Any? {
        val conn = liveRef.channel.newConnection()
        val call: RemoteCall
        try {
            call = StreamRemoteCall(conn, liveRef.id, methodHash)
            //write params
            params.forEach { call.outputStream.writeObject(it) }
        } catch (e: IOException) {
            liveRef.channel.free(conn, false)
            throw MarshalException("failed to write params", e)
        }
        try {
            //execute call. If encounter exception, it will be thrown here
            call.executeCall()
        } catch (e: Exception) {
            //if is a remote exception, reuse connection
            liveRef.channel.free(conn, e === call.serverException)
            throw e
        }
        //read return value
        val result: Any?
        try {
            result = call.inputStream.readObject()
        } catch (e: Exception) {
            liveRef.channel.free(conn, false)
            throw MarshalException("failed to read result", e)
        }
        liveRef.channel.free(conn, true)
        return result
    }

}