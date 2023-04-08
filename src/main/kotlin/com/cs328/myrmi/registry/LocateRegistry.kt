package com.cs328.myrmi.registry

import com.cs328.myrmi.server.ObjID
import com.cs328.myrmi.server.UnicastRef
import com.cs328.myrmi.server.Util
import com.cs328.myrmi.transport.LiveRef
import com.cs328.myrmi.transport.tcp.TCPEndpoint
import java.net.InetAddress

class LocateRegistry private constructor() {
    companion object {

        /** get a registry proxy on a remote host */
        fun getRegistry(host: String, port: Int): Registry {
            //check whether to use default port
            val remotePort = if (port < 0) Registry.REGISTRY_PORT else port
            //use default host if empty
            val remoteHost = host.ifEmpty { InetAddress.getLocalHost().hostName }
            val liveRef = LiveRef(ObjID(ObjID.REGISTRY_ID), TCPEndpoint(remoteHost, remotePort))
            return Util.createProxy(Registry::class.java, UnicastRef(liveRef)) as Registry
        }

        @JvmStatic
        /** create a registry on local host */
        fun createRegistry(port: Int): Registry {
            return RegistryImpl(port)
        }
    }

}