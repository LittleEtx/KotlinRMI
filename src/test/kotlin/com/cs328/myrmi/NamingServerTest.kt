package com.cs328.myrmi

import com.cs328.myrmi.registry.LocateRegistry
import com.cs328.myrmi.server.UnicastRemoteObject

fun main() {
    LocateRegistry.createRegistry(8080)
    val impl = TestRemoteImpl()
    UnicastRemoteObject.exportObject(impl)
    Naming.rebind("rmi://localhost:8080/test", impl)
}