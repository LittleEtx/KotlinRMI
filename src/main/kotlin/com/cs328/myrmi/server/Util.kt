package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class Util {
    companion object {
        fun getMethodHash(method: Method): Long {
            val sb = StringBuilder()
            sb.append(method.name.split(".").last())
            sb.append("(")
            method.parameterTypes.forEach { sb.append(it.name) }
            sb.append(")")
            return sb.toString().hashCode().toLong()
        }

        fun getRemoteInterfaces(implClass: Class<*>): List<Class<*>> {
            var clazz = implClass
            val interfaces = mutableListOf<Class<*>>()
            if (clazz.isInterface && Remote::class.java.isAssignableFrom(clazz)) {
                interfaces.add(clazz)
            }
            while (clazz.superclass != null) {
                interfaces.addAll(clazz.interfaces.filter { Remote::class.java.isAssignableFrom(it) })
                clazz = clazz.superclass
            }
            return interfaces
        }

        fun createProxy(implClass: Class<*>, clientRef: RemoteRef): Any {
            //check if implClass is a remote object
            val interfaces = getRemoteInterfaces(implClass)
            if (interfaces.isEmpty()) {
                throw IllegalArgumentException("object does not implement Remote interface!")
            }
            return Proxy.newProxyInstance(
                implClass.classLoader,
                interfaces.toTypedArray(),
                RemoteObjectInvocationHandler(clientRef))
        }

    }


}