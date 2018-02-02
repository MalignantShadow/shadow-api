package info.malignantshadow.api.commands.dispatch

import info.malignantshadow.api.commands.Command
import info.malignantshadow.api.commands.Flag
import info.malignantshadow.api.commands.parse.CommandInput

class Context(
        val source: Source,
        val cmd: Command,
        val givenArgs: List<CommandInput>
) {

    val params = givenArgs.filter { it.key != null && it.key !is Flag }

    val flags = givenArgs.filter { it.key != null && it.key is Flag }

    val extra = givenArgs.filter { it.key == null }.map { it.input!! }

    fun component1() = source

    fun component2() = cmd

    fun component3() = params

    fun component4() = flags

    fun component5() = extra

    fun flagIsPresent(name: String) = flags.any { it.key?.name == name }

    operator fun get(name: String) = params.firstOrNull { it.key?.name == name }?.value

    fun getFlagValue(name: String) = flags.firstOrNull { it.key?.name == name }?.value

    inline fun <R, reified T> get(key: String, fn: (T) -> R): R {
        val value = get(key)
        return fn(value as T)
    }

    inline fun <R, reified T, reified U> get(key1: String, key2: String, fn: (T, U) -> R): R {
        val v1 = get(key1)
        val v2 = get(key2)
        return fn(v1 as T, v2 as U)
    }

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