package com.cs328.myrmi.server

import java.io.Serializable
import kotlin.random.Random

class ObjID(
    val id: Long = Random.nextLong()
) : Serializable {
    companion object {
        private const val serialVersionUID = -5092223564114849833L
    }
}