package com.cs328.myrmi.exception

/**
 * The exception is thrown when the RMI runtime is unable to unmarshal the
 * input stream parameter or return value of a remote call.
 */

class UnmarshalException: RemoteException {
    constructor(message: String, cause: Exception) : super(message, cause)
    constructor(message: String) : super(message)
}