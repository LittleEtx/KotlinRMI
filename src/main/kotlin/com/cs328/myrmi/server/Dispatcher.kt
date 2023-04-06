package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.transport.RemoteCall

interface Dispatcher {
    fun dispatch(obj: Remote, call: RemoteCall)
}