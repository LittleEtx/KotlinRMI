package com.cs328.myrmi.exception

/**
 * The exception that is thrown when an error is thrown by the server
 * when processing a remote call.
 */
class ServerError : RemoteException {
    constructor(msg: String, cause: Error) : super(msg, cause)

}