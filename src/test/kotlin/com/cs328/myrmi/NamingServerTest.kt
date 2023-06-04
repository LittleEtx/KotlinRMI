package com.cs328.myrmi

import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.server.UnicastRemoteObject

fun main() {
    RMILogger.parentLogger.record()
    val impl = TestRemoteImpl()
    UnicastRemoteObject.exportObject(impl)
    Naming.rebind("rmi://localhost:8080/test", impl)
}