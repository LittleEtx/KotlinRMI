package com.cs328.myrmi

import com.cs328.myrmi.exception.AlreadyBoundException
import com.cs328.myrmi.exception.MalformedURLException
import com.cs328.myrmi.registry.LocateRegistry
import java.net.URI

/**
 * Using the Naming class, you can bind and lookup remote objects.
 */
@Suppress("unused")
object Naming {
    /**
     * Binds the specified url to a remote object.
     * @throws AlreadyBoundException
     */
    @JvmStatic
    fun bind(url: String, obj: Remote) {
        val (host, port, name) = parseURL(url)
        LocateRegistry.getRegistry(host, port).bind(name, obj)
    }

    @JvmStatic
    fun unbind(url: String) {
        val (host, port, name) = parseURL(url)
        LocateRegistry.getRegistry(host, port).unbind(name)
    }

    @JvmStatic
    fun rebind(url: String, obj: Remote) {
        val (host, port, name) = parseURL(url)
        LocateRegistry.getRegistry(host, port).rebind(name, obj)
    }

    @JvmStatic
    fun lookup(url: String): Remote {
        val (host, port, name) = parseURL(url)
        return LocateRegistry.getRegistry(host, port).lookup(name)
    }


    @JvmStatic
    fun list(url: String): List<String> {
        val (host, port, _) = parseURL(url)
        val prefix = "${if (port > 0) "$host:$port" else host}/}"
        return LocateRegistry.getRegistry(host, port).list().map { "$prefix$it" }
    }

    private data class Destination(
        val host: String,
        val port: Int,
        val name: String)

    private fun parseURL(uriStr: String): Destination {
        val uri = URI(uriStr)
        if (uri.isOpaque) {
            throw MalformedURLException("not a hierarchical URL: $uriStr")
        }
        if (uri.fragment != null) {
            throw MalformedURLException("invalid character '#' in URL: $uriStr")
        }
        if (uri.query != null) {
            throw MalformedURLException("invalid character '?' in URL: $uriStr")
        }
        if (uri.userInfo != null) {
            throw MalformedURLException("invalid character '@' in URL: $uriStr")
        }

        if (uri.scheme != null && uri.scheme != "rmi") {
            throw MalformedURLException("invalid scheme: ${uri.scheme}")
        }

        val name = if (uri.path.startsWith("/")) uri.path.substring(1) else uri.path
        val host = uri.host ?: "localhost"
        val port = uri.port
        return Destination(host, port, name)
    }
}