package com.cs328.myrmi.server

import com.cs328.myrmi.exception.MarshalException
import com.cs328.myrmi.exception.RemoteException
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.transport.LiveRef
import com.cs328.myrmi.transport.StreamRemoteCall
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.lang.reflect.Method

/**
 * stub class in the client
 */
open class UnicastRef(val liveRef: LiveRef) : RemoteRef {
    private val logger by lazy {  RMILogger.of(UnicastRef::class.java.name) }
    override fun invoke(method: Method, params: Array<Any?>): Any? {
        logger.info("Client invoke remote method")

        //check params match method
        if (method.parameterCount != params.size) {
            throw RemoteException("internal exception: parameter count mismatch")
        }

        val conn = liveRef.channel.newConnection()
        logger.fine("connection established")
        val call: RemoteCall
        try {
            call = StreamRemoteCall(conn, liveRef.id, Util.getMethodHash(method))
            //write params
            params.forEach { call.outputStream.writeObject(it) }
            call.releaseOutputStream()
            logger.fine("parameters written, wait for remote server execute")
        } catch (e: IOException) {
            liveRef.channel.free(conn, false)
            throw MarshalException("failed to write params", e)
        }
        try {
            //execute call. If encounter exception, it will be thrown here
            call.executeCall()
        } catch (e: Exception) {
            //if is a remote exception, reuse connection
            logger.info("receive exception from remote")
            liveRef.channel.free(conn, e === call.serverException)
            throw e
        }
        logger.fine("receive return value from remote")
        //read return value
        val result: Any?
        try {
            result = call.inputStream.readObject()
        } catch (e: Exception) {
            liveRef.channel.free(conn, false)
            throw MarshalException("failed to read result", e)
        } finally {
            call.releaseInputStream()
        }
        liveRef.channel.free(conn, true)
        return result
    }

    override val remoteToString: String
        get() = "${javaClass.name.split(".").last()}[$liveRef]"
    override val remoteHashCode: Int
        get() = liveRef.hashCode()

    override fun remoteEquals(other: Any?): Boolean {
        return other is UnicastRef && liveRef.remoteEquals(other.liveRef)
    }

    override fun writeExternal(out: ObjectOutput?) {
        TODO("Not yet implemented")
    }

    override fun readExternal(`in`: ObjectInput?) {
        TODO("Not yet implemented")
    }

}