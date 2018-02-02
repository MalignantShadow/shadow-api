package info.malignantshadow.api.commands.dispatch

import info.malignantshadow.api.commands.Command

typealias SourceRequirement = (Source, Command) -> Boolean

open class Source {

    open fun hasPermission(permission: String) = true

    open fun print(message: String, vararg args: Any?) =
        println(message.format(*args))

    open fun printErr(message: String, vararg args: Any?) =
            System.err.println(message.format(*args))

    object Requirements {

        val HAS_PERMISSION = { source: Source, cmd: Command -> source.hasPermission(cmd.permission) }

        fun all(requirements: Iterable<SourceRequirement>) = { source: Source, cmd: Command ->
            requirements.all { it(source, cmd) }
        }

        fun all(first: SourceRequirement, second: SourceRequirement, vararg others: SourceRequirement) =
                all(listOf(first, second, *others))

        fun any(requirements: Iterable<SourceRequirement>) = { source: Source, cmd: Command ->
            requirements.any { it(source, cmd) }
        }

        fun any(first: SourceRequirement, second: SourceRequirement, vararg others: SourceRequirement) =
                any(listOf(first, second, *others))

        fun none(requirements: Iterable<SourceRequirement>) = { source: Source, cmd: Command ->
            requirements.none { it(source, cmd) }
        }

        fun none(first: SourceRequirement, second: SourceRequirement, vararg others: SourceRequirement) =
                none(listOf(first, second, *others))

    }

}