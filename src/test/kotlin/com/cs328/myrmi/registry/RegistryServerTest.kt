package com.cs328.myrmi.registry

import com.cs328.myrmi.TestRemoteImpl
import com.cs328.myrmi.runtime.RMILogger
import com.cs328.myrmi.server.UnicastServerRef
import java.util.logging.ConsoleHandler
import java.util.logging.Level

fun main() {
    val logger = RMILogger.of(UnicastServerRef::class.java.name)
    logger.level = Level.ALL
    val handler = ConsoleHandler()
    handler.level = Level.ALL
    logger.addHandler(handler)

    val registry = LocateRegistry.createRegistry(8080)
    registry.rebind("test", TestRemoteImpl())
}