package info.malignantshadow.api.commands

import info.malignantshadow.api.commands.dispatch.CommandContext
import info.malignantshadow.api.commands.dispatch.CommandResult
import info.malignantshadow.api.commands.dispatch.CommandSource
import info.malignantshadow.api.commands.parse.CommandElement

class CommandSpec(
        val name: String,
        val aliases: List<String>,
        val desc: String,
        val params: List<CommandParameter>,
        val extra: CommandParameter?,
        val handler: ((CommandContext) -> CommandResult?)?,
        val children: List<CommandSpec>,
        val isSendableBy: (CommandSource) -> Boolean,
        val isHiddenFor: (CommandSource) -> Boolean
) {

    val allAliases = listOf(name, *aliases.toTypedArray())
    val flags = params.filter { it.isFlag }
    val isParent = !children.isEmpty()
    val minArgs = params.filter { !it.isFlag }.count { it.isRequired }

    val minFlags = params.filter { it.isFlag }.count { it.isRequired && !it.types.isEmpty() }

    init {
        allAliases.forEach {
            check(!it.startsWith("-")) { "A command cannot have an alias that starts with '-' (given: '$it')" }
            check(Regex("\\s") !in it) { "A command cannot have whitespace in any of its aliases" }
        }
        check(handler != null || !children.isEmpty()) {
            "Command '$name' is empty - a command must have a handler or at least one child command"
        }
    }

    fun hasAlias(alias: String) = allAliases.firstOrNull { it.equals(alias, true) } != null

    fun getVisibleChildren(source: CommandSource) = children.filter { !it.isHiddenFor(source) }
    fun getSendableChildren(source: CommandSource) = children.filter { it.handler != null && it.isSendableBy(source) }

    // TODO: DOCS | args not checked for validity
    fun dispatch(source: CommandSource, args: List<CommandElement>) {
        check(handler != null) { "This command does not have a handler" }
        handler!!(CommandContext(this, source, args))
    }


}