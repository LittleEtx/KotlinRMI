package com.cs328.myrmi.exception

/**
 * This exception is thrown when the server encounters an exception
 * on processing remote calls.
 */
class ServerException: RemoteException {
    constructor(msg: String) : super(msg)
    constructor(msg: String, cause: Throwable) : super(msg, cause)
}