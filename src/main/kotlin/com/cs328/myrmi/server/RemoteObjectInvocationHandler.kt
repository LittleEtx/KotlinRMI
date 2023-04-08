package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class RemoteObjectInvocationHandler(remoteRef: RemoteRef) : RemoteObject(remoteRef), InvocationHandler {

    /**
     * This method is called when a method is invoked on a proxy instance
     */
    override fun invoke(proxy: Any?, method: Method?, args: Array<Any?>?): Any? {
        //ensure proxy is bound to this invocation handler
        if (!Proxy.isProxyClass(proxy?.javaClass)) {
            throw IllegalArgumentException("not a proxy!")
        }
        if (Proxy.getInvocationHandler(proxy) != this) {
            throw IllegalArgumentException("not bound to this invocation handler!")
        }
        method ?: throw IllegalArgumentException("method cannot be null!")
        //check if invoke remote method or object method
        return if (method.declaringClass == Any::class.java) {
            invokeObjectMethod(proxy, method, args)
        } else if (method.name == "finalize" && method.parameterCount == 0) {
            null //do nothing
        } else {
            invokeRemoteMethod(proxy, method, args)
        }
    }

    /** object methods are not called remotely */
    private fun invokeObjectMethod(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        return when (method.name) {
            "toString" -> proxy?.getString()
            "hashCode" -> hashCode() //hashcode of this remote object
            //equals if proxy equals or handler equals
            "equals" -> proxy == args?.get(0) ||
                    (Proxy.isProxyClass(args?.get(0)?.javaClass)
                    && Proxy.getInvocationHandler(args?.get(0)) == this)
            else -> throw IllegalArgumentException("unknown object method ${method.name}")
        }
    }

    private fun invokeRemoteMethod(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        //check if proxy remote
        if (proxy !is Remote) {
            throw IllegalArgumentException("proxy is not a remote object!")
        }
        //check if method is remote
        if (!Remote::class.java.isAssignableFrom(method.declaringClass)) {
            throw IllegalArgumentException("method is not a remote method!")
        }
        //this invocation may throw remote exception
        return remoteRef.invoke(method, args ?: emptyArray())
    }

    private fun Any.getString(): String {
        val interfaceName = this::class.java.interfaces.find { it.name != "com.cs328.myrmi.Remote" }
        return "Proxy[${interfaceName?.name?.split(".")?.last()}, ${this@RemoteObjectInvocationHandler}]"
    }
}