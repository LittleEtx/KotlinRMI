package com.cs328.myrmi.server

import java.io.Serializable
import kotlin.random.Random

class ObjID(
    val id: Long
) : Serializable {
    companion object {
        private const val serialVersionUID = -5092223564114849833L

        fun new(): ObjID {
            return ObjID(Random.nextLong())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ObjID) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "ObjID(id=$id)"
    }
}