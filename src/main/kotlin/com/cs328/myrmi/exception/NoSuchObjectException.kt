package com.cs328.myrmi.exception

class NoSuchObjectException: RemoteException {
    constructor(message: String, cause: Exception) : super(message, cause)
    constructor(message: String) : super(message)
}