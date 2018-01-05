package info.malignantshadow.api.commands

import info.malignantshadow.api.util.arguments.ParsedArguments

open class CommandContext(
        val prefix: String,
        val sender: CommandSender?,
        val cmd: Command,
        val parsedArgs: ParsedArguments
) {

    val inputJoined = getInputJoined()
    val extraJoined = getExtraJoined()
    val fullCommandString = "$prefix $inputJoined $extraJoined"

    fun getInputJoined(delimiter: String = " ") = parsedArgs.input.joinToString(delimiter)
    fun getExtraJoined(delimiter: String = " ") = parsedArgs.extra.joinToString(delimiter)

    operator fun contains(name: String) = name in parsedArgs
    fun hasInputFor(name: String) = getArg(name)?.input != null ?: false

    operator fun get(name: String) = parsedArgs.getValue(name)
    fun getArg(name: String) = parsedArgs[name]

    fun dispatchSelf() = cmd.handler?.invoke(this) != null

}