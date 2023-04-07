package com.cs328.myrmi.exception

/**
 * This exception is thrown when an object is not bound in the registry.
 */
class NotBoundException: RemoteException {
    constructor(message: String, cause: Exception) : super(message, cause)
    constructor(message: String) : super(message)
}