package com.cs328.myrmi.server

import java.lang.reflect.Method

/**
 * Remote object reference for invoking remote methods.
 * Remote objects are identified by a LiveRef.
 */
interface RemoteRef {
    fun invoke(method: Method, params: Array<Any?>): Any?

}