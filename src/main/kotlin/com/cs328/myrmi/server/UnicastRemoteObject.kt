package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.transport.LiveRef

/**
 * A remote object must be exported through this class.
 * One can choose to inherit the class or use the static method to export an object.
 */
open class UnicastRemoteObject(port: Int) :
    RemoteObject(UnicastServerRef(LiveRef(ObjID.new(), port)))
{
    constructor() : this(0)
    init {
        exportObject(this, remoteRef as UnicastServerRef)
    }

    companion object {
        fun exportObject(obj: Remote): Remote {
            return exportObject(obj, 0)
        }
        fun exportObject(obj: Remote, port: Int): Remote {
            val serverRef = UnicastServerRef(LiveRef(ObjID.new(), port))
            return serverRef.exportObject(obj, true)
        }

        private fun exportObject(obj: Remote, remoteRef: UnicastServerRef): Remote {
            return remoteRef.exportObject(obj, true)
        }
    }

}