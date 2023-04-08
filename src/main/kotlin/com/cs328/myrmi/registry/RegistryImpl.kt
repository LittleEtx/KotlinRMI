package com.cs328.myrmi.registry

import com.cs328.myrmi.Remote
import com.cs328.myrmi.exception.AlreadyBoundException
import com.cs328.myrmi.exception.NotBoundException
import com.cs328.myrmi.server.ObjID
import com.cs328.myrmi.server.UnicastServerRef
import com.cs328.myrmi.transport.LiveRef

class RegistryImpl(port: Int) : Registry {
    companion object {
        val id = ObjID(ObjID.REGISTRY_ID)
    }

    init {
        //create skeleton and export itself
        val liveRef = LiveRef(id, port)
        val serverRef = UnicastServerRef(liveRef)
        serverRef.exportObject(this, true)
    }

    private val bindings = mutableMapOf<String, Remote>()

    override fun bind(name: String, obj: Remote) {
        synchronized(bindings) {
            if (bindings.containsKey(name))
                throw AlreadyBoundException("Name $name already bound")
            bindings[name] = obj
        }
    }

    override fun unbind(name: String) {
        synchronized(bindings) {
            if (!bindings.containsKey(name))
                throw NotBoundException("Name $name not bound")
            bindings.remove(name)
        }
    }

    override fun rebind(name: String, obj: Remote) {
        synchronized(bindings) {
            bindings[name] = obj
        }
    }

    override fun lookup(name: String): Remote {
        synchronized(bindings) {
            if (!bindings.containsKey(name))
                throw NotBoundException("Name $name not bound")
            return bindings[name]!!
        }
    }

    override fun list(): List<String> {
        synchronized(bindings) {
            return bindings.keys.toList()
        }
    }
}