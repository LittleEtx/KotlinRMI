package com.cs328.myrmi.runtime

import java.util.logging.Logger

object RMILogger {
    private val loggers = mutableMapOf<String, Logger>()

    fun of(name: String): Logger {
        return loggers.getOrPut(name) {
            val logger = Logger.getLogger(name)
            logger.parent = parentLogger
            logger
        }
    }

    /** parent logger of all MyRmi loggers, ensure that no extra output is printed to console */
    val parentLogger = run {
        val logger = Logger.getLogger("com.cs328.myrmi")
        logger.useParentHandlers = false
        logger
    }!!
}