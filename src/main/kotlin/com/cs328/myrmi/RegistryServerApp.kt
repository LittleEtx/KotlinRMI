package com.cs328.myrmi

import com.cs328.myrmi.registry.LocateRegistry
import com.cs328.myrmi.runtime.RMILogger
import java.util.logging.ConsoleHandler
import java.util.logging.Level

fun main() {
    val logger = RMILogger.parentLogger
    logger.level = Level.ALL
    val handler = ConsoleHandler()
    handler.level = Level.ALL
    logger.addHandler(handler)
    logger.fine("Welcome to MyRMI. Starting registry server...")
    LocateRegistry.createRegistry(8080)
}