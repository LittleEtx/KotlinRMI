package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import java.io.Serializable

abstract class RemoteObject(val remoteRef: RemoteRef) : Remote, Serializable {
    companion object {
        private const val serialVersionUID = -6769863665021973788L
    }

    override fun equals(other: Any?): Boolean {
        return if (other is RemoteObject) {
            remoteRef.remoteEquals(other.remoteRef)
        } else {
            other == this
        }
    }

    override fun hashCode(): Int {
        return remoteRef.remoteHashCode
    }

    override fun toString(): String {
        return "${javaClass.name.split(".").last()}[${remoteRef.remoteToString}]"
    }
}