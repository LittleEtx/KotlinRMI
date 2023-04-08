package com.cs328.myrmi.server

import com.cs328.myrmi.Naming
import com.cs328.myrmi.TestRemoteImpl
import com.cs328.myrmi.TestRemoteInterface
import com.cs328.myrmi.registry.LocateRegistry.Companion.createRegistry

fun main() {
    createRegistry(8080)
    val impl: TestRemoteInterface = TestRemoteImpl()
    UnicastRemoteObject.exportObject(impl)
    Naming.rebind("rmi://localhost:8080/test", impl)
     val test = Naming.lookup("rmi://localhost:8080/test") as TestRemoteInterface
    test.print("Testing remote naming begins")
    UnicastRemoteObject.closeObject(impl)
    test.print("Testing remote naming begins")
}