package com.cs328.myrmi

import com.cs328.myrmi.registry.LocateRegistry
import com.cs328.myrmi.runtime.RMILogger

fun main() {
    RMILogger.parentLogger.record()
    LocateRegistry.createRegistry(8080)
}