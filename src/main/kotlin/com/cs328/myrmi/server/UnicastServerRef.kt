package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.exception.RemoteException
import com.cs328.myrmi.exception.ServerError
import com.cs328.myrmi.exception.ServerException
import com.cs328.myrmi.exception.UnmarshalException
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.transport.LiveRef
import com.cs328.myrmi.transport.Target
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * This class acts like a stub and skeleton at the server
 */
class UnicastServerRef(liveRef: LiveRef) : UnicastRef(liveRef), Dispatcher {
    @delegate: Transient
    private val logger by lazy { RMILogger.of(UnicastServerRef::class.java.name) }
    @Transient
    private lateinit var methodCache: Map<Long, Method>

    companion object {
        val remoteClassMethods = mutableMapOf<Class<*>, Map<Long, Method>>()
    }

    /**
     * Dispatch the call
     */
    override fun dispatch(obj: Remote, call: RemoteCall) {
        logger.info("exported object ${liveRef.id} dispatching call $call")
        try {
            val input = call.inputStream
            val methodHash: Long
            try {
                methodHash = input.readObject() as Long
            } catch (e: Exception) {
                throw UnmarshalException("failed to read method hash", e)
            }

            val method = methodCache[methodHash] ?: throw UnmarshalException("Unrecognized method hash: $methodHash")
            logger.fine("exported object ${liveRef.id} ready to invoke method $method, now read paras")

            //read params
            val params: Array<Any?>
            try {
                params = method.parameters.map { input.readObject() }.toTypedArray()
            } catch (e: Exception) {
                throw UnmarshalException("failed to read params", e)
            }

            logger.fine("exported object ${liveRef.id} successfully read all paras")

            //invoke method
            val result: Any?
            try {
                result = method.invoke(obj, *params)
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
            logger.fine("method successfully executed on ${liveRef.id}, return value $result")
            //write result
            try {
                call.getResultStream(true).writeObject(result)
            } catch (e: IOException) {
                throw RemoteException("failed to write result on ${liveRef.id}", e)
            }

        } catch (e: Throwable) {
            logger.fine("invoking method on ${liveRef.id} throws exception")
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
    fun exportObject(obj: Remote, permanent: Boolean): Remote {
        logger.info("exporting object $obj, permanent = $permanent")
        val stub = Util.createProxy(obj::class.java, UnicastRef(liveRef)) as Remote
        val target = Target(obj, liveRef.id, stub, this, permanent)
        liveRef.exportObject(target)

        //cache method
        synchronized(remoteClassMethods) {
            methodCache = remoteClassMethods.getOrPut(obj.javaClass) {
                obj.javaClass.interfaces
                    .filter { Remote::class.java.isAssignableFrom(it) }
                    .fold (mutableMapOf()) { acc, clazz ->
                        acc.putAll(clazz.methods.map { Util.getMethodHash(it) to it })
                        acc
                    }
            }
        }

        logger.fine("cache methods: \n" + methodCache.map { "${it.key}-${it.value}"}.joinToString("\n"))
        return stub
    }
}