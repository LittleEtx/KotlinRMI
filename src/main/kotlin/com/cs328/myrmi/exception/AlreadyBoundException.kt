package com.cs328.myrmi.exception

/**
 * This exception is thrown when an object is already bound in the registry.
 */
class AlreadyBoundException : RemoteException {
    constructor(message: String, cause: Exception) : super(message, cause)
    constructor(message: String) : super(message)
}