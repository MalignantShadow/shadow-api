package info.malignantshadow.api.util

const val WIN_32 = 1
const val WIN_64 = 1 shl 1
const val WINDOWS = WIN_32 or WIN_64
const val MAC_32 = 1 shl 2
const val MAC_64 = 1 shl 3
const val MAC = MAC_32 or MAC_64
const val LINUX_32 = 1 shl 4
const val LINUX_64 = 1 shl 5
const val LINUX = LINUX_32 or LINUX_64
const val OTHER_32 = 1 shl 6
const val OTHER_64 = 1 shl 7
const val OTHER = OTHER_32 or OTHER_64
const val ANY_32 = WIN_32 or MAC_32 or LINUX_32 or OTHER_32
const val ANY_64 = WIN_64 or MAC_64 or LINUX_64 or OTHER_64

val currentPlatform: Int = {
    val name = System.getProperty("os.name")
    val arch = System.getProperty("os.arch")
    val is32 = arch.endsWith("86")
    if (name.startsWith("win", true))
        if (is32) WIN_32 else WIN_64
    else if (name.startsWith("mac", true))
        if (is32) MAC_32 else MAC_64
    else if (name.startsWith("linux"))
        if (is32) LINUX_32 else LINUX_64
    else
        if (is32) OTHER_32 else OTHER_64
}()

inline fun <T> build(receiver: T, init: T.() -> Unit): T {
    receiver.init()
    return receiver
}

inline fun ifPlatform(platforms: Int, block: () -> Unit) = if (currentPlatform in platforms) block() else Unit

fun Any?.equalsAny(vararg tests: Any?) = tests.indexOfFirst { it == this } >= 0
