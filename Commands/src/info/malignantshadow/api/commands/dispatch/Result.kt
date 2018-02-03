package info.malignantshadow.api.commands.dispatch

/**
 * Represents a command result.
 */
interface Result

/**
 * Represents a boolean result.
 *
 * @param result The result
 */
data class BooleanResult(val result: Boolean) : Result

/**
 * Represents a number result.
 *
 * @param result The result
 */
data class NumberResult(val result: Number) : Result

/**
 * Represents a string result.
 *
 * @param result The result
 */
data class StringResult(val result: String) : Result

/**
 * Represents a result that has no value associated with it. The supplied Int should
 * represent a constant that is meaningful to the programmer - such as
 * `PLAYER_NOT_FOUND`, or `SUCCESS`
 *
 * @param result The result
 */
data class TypeResult(val result: Int) : Result

/**
 * Represents that an error occurred while the command was running. Note that the message
 * is not actually sent to the command source
 *
 * @param ctx The context of the command
 * @param message The error message
 */
data class CommandError(val ctx: Context, val message: String = "") : Result
