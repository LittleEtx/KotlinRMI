package com.cs328.myrmi.server

import java.lang.reflect.Method

class Util {
    companion object {
        fun getMethodHash(method: Method): Long {
            val sb = StringBuilder()
            sb.append(method.name.split(".").last())
            sb.append("(")
            method.parameterTypes.forEach { sb.append(it.name) }
            sb.append(")")
            return sb.toString().hashCode().toLong()
        }



    }


}