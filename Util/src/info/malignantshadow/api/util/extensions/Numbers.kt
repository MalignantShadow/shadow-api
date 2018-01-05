@file:JvmMultifileClass
@file:JvmName("Extensions")
@file:Suppress("unused")

package info.malignantshadow.api.util.extensions

fun Int.clamp(min: Int, max: Int) = Math.max(min, Math.min(max, this))
fun Long.clamp(min: Long, max: Long) = Math.max(min, Math.min(max, this))
fun Double.clamp(min: Double, max: Double) = Math.max(min, Math.min(max, this))
fun Float.clamp(min: Float, max: Float) = Math.max(min, Math.min(max, this))