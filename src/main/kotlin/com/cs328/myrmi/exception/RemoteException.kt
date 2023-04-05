package com.cs328.myrmi.exception

import java.io.IOException

/**
 * The base class for all remote exceptions.
 */
open class RemoteException : IOException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message) {
        this.initCause(cause)
    }
    constructor(cause: Throwable) : super() {
        this.initCause(cause)
    }
}