package com.cs328.myrmi.transport

object TransportConstants {
    /** The magic number for the protocol. */
    const val MAGIC = 0x4D59524D
    /** protocol version */
    const val VERSION = 0x0001

    /** indicate stream connection */
    const val STREAM_PROTOCOL = 0x4b
    /** indicate single op connection, no ack required */
    const val SINGLE_OP_PROTOCOL = 0x4c
    /** indicate multiplexed connection*/
    const val MULTIPLEX_PROTOCOL = 0x4d
    /** accept protocol */
    const val PROTOCOL_ACK = 0x4e
    /** protocol not supported */
    const val PROTOCOL_NACK = 0x4f

    /** indicate a call action */
    const val CALL = 0x50
    /** the return of remote call */
    const val RETURN = 0x51
    /** indicate a ping action */
    const val PING = 0x52
    /** the return of ping */
    const val PING_ACK = 0x53
    /** indicate a DGC release action */
    const val DGC_ACK = 0x54
    /** normal return of RMI */
    const val NORMAL_RETURN = 0x01
    /** exception return of RMI */
    const val EXCEPTION_RETURN = 0x02
}