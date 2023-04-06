package com.cs328.myrmi.server

import com.cs328.myrmi.Remote

interface Dispatcher {
    fun dispatch(obj: Remote, call: RemoteCall)
}