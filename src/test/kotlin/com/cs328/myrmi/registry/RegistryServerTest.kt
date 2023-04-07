package com.cs328.myrmi.registry

import com.cs328.myrmi.TestRemoteImpl
import com.cs328.myrmi.record
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.server.MarshalOutputStream
import com.cs328.myrmi.server.UnicastRemoteObject
import com.cs328.myrmi.server.UnicastServerRef
import com.cs328.myrmi.transport.ObjTable

fun main() {
    RMILogger.of(UnicastServerRef::class.java.name).record()
    RMILogger.of(MarshalOutputStream::class.java.name).record()
    RMILogger.of(ObjTable::class.java.name).record()

    val test = TestRemoteImpl(4)
    UnicastRemoteObject.exportObject(test)
    val test2 = TestRemoteImpl(7)
    UnicastRemoteObject.exportObject(test2)
    val registry = LocateRegistry.createRegistry(8080)
    registry.rebind("test", test)
    registry.rebind("test2", test2)
}