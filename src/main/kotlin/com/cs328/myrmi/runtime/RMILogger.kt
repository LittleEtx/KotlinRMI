package com.cs328.myrmi.runtime

import java.util.logging.Logger

class RMILogger {
    companion object {
        fun of(name: String): Logger {
            return Logger.getLogger(name)
        }

        val parentLogger = run {
            val logger = Logger.getLogger("com.cs328.myrmi")
            logger.useParentHandlers = false
            logger
        }
    }
}