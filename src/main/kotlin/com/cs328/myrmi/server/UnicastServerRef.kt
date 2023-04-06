package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.exception.RemoteException
import com.cs328.myrmi.exception.ServerError
import com.cs328.myrmi.exception.ServerException
import com.cs328.myrmi.exception.UnmarshalException
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.transport.LiveRef
import com.cs328.myrmi.transport.RemoteCall
import com.cs328.myrmi.transport.Target
import java.io.IOException
import java.io.ObjectInput
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class UnicastServerRef(liveRef: LiveRef) : UnicastRef(liveRef), Dispatcher {
    private val logger by lazy { RMILogger.of(this::class.java.name) }
    private lateinit var methodCache: Map<Long, Method>

    companion object {
        val remoteClassMethods = mutableMapOf<Class<*>, Map<Long, Method>>()
    }

    /**
     * Dispatch the call
     */
    override fun dispatch(obj: Remote, call: RemoteCall) {
        logger.fine("dispatching call $call")
        val input: ObjectInput
        val methodHash: Long
        try {
            try {
                input = call.inputStream
                methodHash = input.readLong()
            } catch (e: Exception) {
                throw UnmarshalException("failed to read method hash", e)
            }

            val method = methodCache[methodHash] ?: throw UnmarshalException("Unrecognized method hash: $methodHash")
            logger.fine("invoking method $method")

            //read params
            val params: Array<Any?>
            try {
                params = method.parameters.map { input.readObject() }.toTypedArray()
            } catch (e: Exception) {
                throw UnmarshalException("failed to read params", e)
            }

            //invoke method
            val result: Any?
            try {
                result = method.invoke(obj, *params)
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }

            //write result
            try {
                call.getResultStream(true).writeObject(result)
            } catch (e: IOException) {
                throw RemoteException("failed to write result", e)
            }

        } catch (e: Throwable) {
            //write exception
            var exp = e
            if (exp is Error) {
                exp = ServerError("Error occurred in server thread", exp)
            } else if (exp is RemoteException) {
                exp = ServerException("Exception occurred in server thread", exp)
            }
            call.getResultStream(false).writeObject(exp)
        } finally {
            call.releaseInputStream()
            call.releaseOutputStream()
        }
    }

    /**
     * Export the object to make it available to receive incoming calls.
     * This method will create the target and export it on the local endpoint
     */
    fun exportObject(obj: Remote, permanent: Boolean) {
        logger.info("exporting object $obj")

        val target = Target(obj, liveRef.id, this, permanent)
        liveRef.exportObject(target)

        //cache method
        synchronized(remoteClassMethods) {
            methodCache = remoteClassMethods.getOrPut(obj.javaClass) {
                obj.javaClass.classes
                    .filter { Remote::class.java.isAssignableFrom(it) }
                    .fold (mutableMapOf()) { acc, clazz ->
                        acc.putAll(clazz.methods.map { Util.getMethodHash(it) to it })
                        acc
                    }
            }
        }
    }
}