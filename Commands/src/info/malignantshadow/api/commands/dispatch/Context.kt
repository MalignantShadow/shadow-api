package info.malignantshadow.api.commands.dispatch

import info.malignantshadow.api.commands.Command
import info.malignantshadow.api.commands.Flag
import info.malignantshadow.api.commands.parse.CommandInput

/**
 * Represents the context of a command's execution, which includes the source of the command and it's arguments.
 *
 * @author Shad0w (Caleb Downs)
 */
class Context(

        /**
         * The source of the command.
         */
        val source: Source,

        /**
         * The command.
         */
        val cmd: Command,

        /**
         * The arguments given to the command.
         */
        val givenArgs: List<CommandInput>
) {

    /**
     * Arguments given to defined command parameters. This includes all command parameters,
     * even if the parameter was not given any input. (In this case, `input` will be `null`)
     */
    val params = givenArgs.filter { it.key != null && it.key !is Flag }

    /**
     * Arguments given to flags. If a flag was defined by a [Source] more than once,
     * there will be one [CommandInput] for each occurrence of the flag.
     */
    val flags = givenArgs.filter { it.key != null && it.key is Flag }

    /**
     * Arguments supplied to the command that are not associated with any parameter.
     */
    val extra = givenArgs.filter { it.key == null }.map { it.input!! }

    /**
     * The source.
     */
    fun component1() = source

    /**
     * The command.
     */
    fun component2() = cmd

    /**
     * Arguments supplied to the command that are associated with a parameter.
     */
    fun component3() = params

    /**
     * Arguments given to flags. If a flag was defined by a [Source] more than once,
     * there will be one [CommandInput] for each occurrence of the flag.
     */
    fun component4() = flags

    /**
     * Arguments supplied to the command that are not associated with any parameter.
     */
    fun component5() = extra

    /**
     * Indicates whether a flag with the given alias was supplied to the command.
     *
     * @param name The name of the flag
     */
    fun flagIsPresent(name: String) = flags.any { it.key?.name == name }

    /**
     * Gets the value of the parameter with the given name.
     *
     * @param name The name of the parameter
     */
    operator fun get(name: String) = params.firstOrNull { it.key?.name == name }?.value

    /**
     * Gets a list of flags whose names or aliases match any of the given aliases
     *
     * @param name The name of a flag
     * @param others Other flag names
     */
    fun groupFlags(name: String, vararg others: String): List<CommandInput> {
        val givenAliases = listOf(name, *others)
        return flags.filter { f -> givenAliases.any { (f.key as Flag).hasAlias(it) } }
    }

    /**
     * Gets the value of the first flag that has the given alias.
     *
     * @param name The name of a flag
     */
    fun getFlagValue(name: String) = flags.firstOrNull { it.key?.name == name }?.value

    /**
     * Retrieves the value of the parameter with the given name, passes it to the given
     * function, and returns the result.
     *
     * @param key The name of the parameter
     * @param fn The function
     */
    inline fun <R, reified T> get(key: String, fn: (T) -> R): R {
        val value = get(key)
        return fn(value as T)
    }

    /**
     * Retrieves the values of the parameters with the given names, passes it to the given
     * function, and returns the result.
     *
     * @param key1 The name of the first parameter
     * @param key2 The name of the second parameter
     * @param fn The function
     */
    inline fun <R, reified T, reified U> get(key1: String, key2: String, fn: (T, U) -> R): R {
        val v1 = get(key1)
        val v2 = get(key2)
        return fn(v1 as T, v2 as U)
    }

    /**
     * Retrieves the values of the parameters with the given names, passes it to the given
     * function, and returns the result.
     *
     * @param key1 The name of the first parameter
     * @param key2 The name of the second parameter
     * @param key3 The name of the third parameter
     * @param fn The function
     */
    inline fun <R, reified T, reified U, reified V> get(
            key1: String,
            key2: String,
            key3: String,
            fn: (T, U, V) -> R
    ): R {
        val v1 = get(key1)
        val v2 = get(key2)
        val v3 = get(key3)
        return fn(v1 as T, v2 as U, v3 as V)
    }

    /**
     * Retrieves the values of the parameters with the given names, passes it to the given
     * function, and returns the result.
     *
     * @param key1 The name of the first parameter
     * @param key2 The name of the second parameter
     * @param key3 The name of the third parameter
     * @param key4 The name of the fourth parameter
     * @param fn The function
     */
    inline fun <R, reified T, reified U, reified V, reified W> get(
            key1: String,
            key2: String,
            key3: String,
            key4: String,
            fn: (T, U, V, W) -> R
    ): R {
        val v1 = get(key1)
        val v2 = get(key2)
        val v3 = get(key3)
        val v4 = get(key4)
        return fn(v1 as T, v2 as U, v3 as V, v4 as W)
    }

    /**
     * Retrieves the values of the parameters with the given names, passes it to the given
     * function, and returns the result.
     *
     * @param key1 The name of the first parameter
     * @param key2 The name of the second parameter
     * @param key3 The name of the third parameter
     * @param key4 The name of the fourth parameter
     * @param key5 The name of the fifth parameter
     * @param fn The function
     */
    inline fun <R, reified T, reified U, reified V, reified W, reified X> get(
            key1: String,
            key2: String,
            key3: String,
            key4: String,
            key5: String,
            fn: (T, U, V, W, X) -> R
    ): R {
        val v1 = get(key1)
        val v2 = get(key2)
        val v3 = get(key3)
        val v4 = get(key4)
        val v5 = get(key5)
        return fn(v1 as T, v2 as U, v3 as V, v4 as W, v5 as X)
    }

}