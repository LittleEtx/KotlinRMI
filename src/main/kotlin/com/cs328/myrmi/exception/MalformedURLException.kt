package com.cs328.myrmi.exception

/**
 * The exception that is thrown when using a malformed URL in the Naming class.
 */
class MalformedURLException: RemoteException {
    constructor(msg: String) : super(msg)
    constructor(msg: String, cause: Throwable) : super(msg, cause)
}