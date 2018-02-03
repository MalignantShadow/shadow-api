package info.malignantshadow.api.commands

import info.malignantshadow.api.commands.dispatch.Context
import info.malignantshadow.api.commands.dispatch.Result
import info.malignantshadow.api.commands.dispatch.Source
import info.malignantshadow.api.util.aliases.Aliasable

class Command(
        override val name: String,
        override val aliases: List<String>,
        val description: String,
        val permission: String,
        val parameters: List<Parameter>,
        val minArgs: Int,
        val extraArgUsage: String,
        val flags: List<Flag>,
        val sourceRequirement: (Source, Command) -> Boolean,
        children: List<Command>,
        private val handler: ((Context) -> Result?)?,
        val helpFlags: List<String>,
        val helpFn: (Command) -> List<String>
) : CommandContainer(children), Aliasable {

    companion object {

        fun dispatchContext(context: Context): Result? {
            val flag = context.cmd.helpFlags.firstOrNull { context.flagIsPresent(it) }
            if (flag != null) {
                context.cmd.showHelp(context.source)
                return Manager.HelpShownResult(context.source, context.cmd, flag)
            }
            return context.cmd.handler?.invoke(context)
        }

    }

    val allAliases = listOf(name) + aliases

    val joinedAliases = allAliases.joinToString()

    val isParent = children.isNotEmpty()

    val hasFlags = flags.isNotEmpty() || helpFlags.isNotEmpty()

    val isExecutable = handler != null

    // name <required> [optional] [extra..] <options> OR name <sub_command>
    val usage by lazy {
        val list = ArrayList<String>()

        if (isExecutable)
            list.add(buildString {
                append(name)
                parameters.mapIndexed { index, it ->
                    if (parameterIndexIsRequired(index)) "<${it.shownDisplay}>"
                    else "[${it.shownDisplay}]"
                }.forEach { append(" $it") }
                if(extraArgUsage.isNotBlank()) append(" $extraArgUsage")
                if(flags.isNotEmpty())
                    if (flags.count { it.isRequired || it.requiredUnless.isNotEmpty() } > 0)
                    append(" <options..>")
                else
                    append(" [options..]")
            })

        if(isParent)
            list.add("$name <sub_command>")

        if(helpFlags.isNotEmpty())
            list.add("$name ${Flag.getDisplay(helpFlags[0])}")

        list
    }

    private fun parameterIndexIsRequired(index: Int) = index + 1 <= minArgs

    fun isSendableBy(source: Source) = sourceRequirement(source, this)

    fun showHelp(source: Source) = helpFn(this).forEach { source.print(it) }

    fun isRequired(parameter: Parameter) = parameterIndexIsRequired(parameters.indexOf(parameter))


}