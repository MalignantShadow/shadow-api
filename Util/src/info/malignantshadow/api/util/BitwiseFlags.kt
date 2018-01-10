package info.malignantshadow.api.util

/**
 * Indicates whether the integer has any of the given flags
 */
operator fun Int.contains(flags: Int) = (this and flags)> 0

/**
 * Indicates whether the integer has all of the given flags
 */
fun Int.containsAll(flags: Int) = (this and flags) == flags