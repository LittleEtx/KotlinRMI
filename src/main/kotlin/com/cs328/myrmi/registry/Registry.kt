package com.cs328.myrmi.registry

import com.cs328.myrmi.Remote

interface Registry: Remote {
    companion object {
        const val REGISTRY_PORT = 11099
    }
    fun bind(name: String, obj: Remote)
    fun unbind(name: String)

    fun rebind(name: String, obj: Remote)

    fun lookup(name: String): Remote

    fun list(): List<String>
}