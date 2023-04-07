package com.cs328.myrmi.server

import com.cs328.myrmi.Remote
import com.cs328.myrmi.transport.ObjTable
import java.io.ObjectOutputStream
import java.io.OutputStream

/**
 * ObjectOutputStream that enables replacing object writing for remote ref
 */
class MarshalOutputStream(out: OutputStream) : ObjectOutputStream(out) {
    init {
        //enable replacing object writing for remote ref
        enableReplaceObject(true)
    }

    override fun replaceObject(obj: Any?): Any? {
        return if (obj is Remote) {
            //replace obj with remote stub proxy if the object has been exported
            val target = ObjTable.getTarget(obj)
            target?.stub ?: obj
        } else {
            obj
        }
    }

}