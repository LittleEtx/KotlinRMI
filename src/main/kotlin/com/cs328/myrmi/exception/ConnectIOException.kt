package com.cs328.myrmi.exception

/**
 * Exception for IO error
 */
class ConnectIOException : RemoteException {
    constructor(msg: String) : super(msg)
    constructor(msg: String, cause: Exception) : super(msg, cause)
}