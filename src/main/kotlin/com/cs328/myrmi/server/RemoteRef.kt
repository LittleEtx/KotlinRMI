package com.cs328.myrmi.server

import java.io.Serializable
import java.lang.reflect.Method

/**
 * Remote object reference for invoking remote methods.
 * also provides methods for object methods
 * Remote objects are identified by a LiveRef.
 */
interface RemoteRef: Serializable {
    companion object {
        private const val serialVersionUID = -3046849872216343402L
    }

    fun invoke(method: Method, params: Array<Any?>): Any?

    val remoteToString: String
    val remoteHashCode: Int
    fun remoteEquals(other: Any?): Boolean

}