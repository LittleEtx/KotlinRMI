package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.transport.LiveRef
import com.cs328.myrmi.transport.ObjTable

/**
 * A remote object must be exported through this class.
 * One can choose to inherit the class or use the static method to export an object.
 */
open class UnicastRemoteObject(port: Int) :
    RemoteObject(UnicastServerRef(LiveRef(ObjID.new(), port)))
{
    constructor() : this(0)
    init {
        @Suppress("LeakingThis")
        exportObject(this, remoteRef as UnicastServerRef)
    }

    companion object {
        @JvmStatic
        fun exportObject(obj: Remote): Remote {
            return exportObject(obj, 0)
        }
        @JvmStatic
        fun exportObject(obj: Remote, port: Int = 0): Remote {
            val serverRef = UnicastServerRef(LiveRef(ObjID.new(), port))
            return serverRef.exportObject(obj, true)
        }
        private fun exportObject(obj: Remote, remoteRef: UnicastServerRef): Remote {
            return remoteRef.exportObject(obj, true)
        }

        /**
         * Mark the object as no longer being exported.
         * @param force if true, any current connection to the object will be terminated.
         *             Whether true or not no new connections to the object will be allowed.
         */
        @JvmStatic
        fun closeObject(obj: Remote, force: Boolean = false) {
            return ObjTable.removeTarget(obj, force)
        }
    }

}