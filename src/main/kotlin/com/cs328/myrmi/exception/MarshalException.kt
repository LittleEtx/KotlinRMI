package com.cs328.myrmi.exception

/**
 * The exception that is thrown when the marshalling of a remote call fails.
 */
class MarshalException: RemoteException {
    constructor(msg: String) : super(msg)
    constructor(msg: String, cause: Throwable) : super(msg, cause)
}