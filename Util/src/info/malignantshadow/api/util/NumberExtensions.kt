@file:JvmMultifileClass
@file:JvmName("Extensions")
@file:Suppress("unused")

package info.malignantshadow.api.util

/**
 * Clamp an Int
 *
 * @param min The minimum value of the integer
 * @param max The maximum value the integer
 */
fun Int.clamp(min: Int, max: Int) = Math.max(min, Math.min(max, this))

/**
 * Clamp a Long
 *
 * @param min The minimum value of the long
 * @param max The maximum value the long
 */
fun Long.clamp(min: Long, max: Long) = Math.max(min, Math.min(max, this))

/**
 * Clamp a Double
 *
 * @param min The minimum value of the double
 * @param max The maximum value the double
 */
fun Double.clamp(min: Double, max: Double) = Math.max(min, Math.min(max, this))

/**
 * Clamp a Float
 *
 * @param min The minimum value of the float
 * @param max The maximum value the float
 */
fun Float.clamp(min: Float, max: Float) = Math.max(min, Math.min(max, this))