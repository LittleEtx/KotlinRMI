package com.cs328.myrmi

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger

val handler = ConsoleHandler().apply {
    this.level = Level.ALL
}
fun Logger.record() {
    this.level = Level.ALL
    this.addHandler(handler)
}