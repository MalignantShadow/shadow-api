package info.malignantshadow.api.commands

import info.malignantshadow.api.commands.dispatch.Context
import info.malignantshadow.api.commands.dispatch.Source
import info.malignantshadow.api.util.aliases.Aliasable

/**
 * Represents a command.
 *
 * @author Shad0w (Caleb Downs)
 */
class Command(
        override val name: String,
        override val aliases: List<String>,

        /**
         * The description of this command.
         */
        val description: String,

        /**
         * The permission required to execute this command.
         */
        val permission: String,

        /**
         * The parameters of this command.
         */
        val parameters: List<Parameter>,

        /**
         * The minimum amount of arguments required to run this command.
         */
        val minArgs: Int,

        /**
         * The extra arguments usage string.
         */
        val extraArgUsage: String,

        /**
         * The options of this command.
         */
        val options: List<Option>,

        val operandRelation: Int,
        private val sourceRequirement: (Source, Command) -> Boolean,
        children: List<Command>,

        private val handler: ((Context) -> Any?)?,

        /**
         * The names of options which, if present, cause the command to show help.
         */
        val helpOptions: List<String>,
        private val helpFn: (Source, Command) -> List<String>
) : CommandContainer(children), Aliasable {

    companion object {

        fun dispatchContext(context: Context): Any? {
            val flag = context.cmd.helpOptions.firstOrNull { context.optionIsPresent(it) }
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

    val hasOptions = options.isNotEmpty() || helpOptions.isNotEmpty()

    val isExecutable = handler != null

    // name <required> [optional] [extra..] <options> OR name <sub_command>
    val usage by lazy {
        val list = ArrayList<String>()

        if (isExecutable)
            list.add(buildString {
                val options =
                        if (options.isNotEmpty())
                            if (options.count { it.isRequired || it.requiredUnless.isNotEmpty() } > 0)
                                " <options..>"
                            else
                                " [options..]"
                        else ""
                append(name)
                parameters.mapIndexed { index, it ->
                    if (parameterIndexIsRequired(index)) "<${it.shownDisplay}>"
                    else "[${it.shownDisplay}]"
                }.forEach { append(" $it") }
                if (operandRelation == Manager.OPERANDS_LAST || operandRelation == Manager.OPERANDS_MIXED) append(options)
                if (extraArgUsage.isNotBlank()) append(" $extraArgUsage")
                if (operandRelation == Manager.OPERANDS_FIRST) append(options)
            })

        if (isParent)
            list.add("$name <sub_command>")

        if (helpOptions.isNotEmpty())
            list.add("$name ${Option.getDisplay(helpOptions[0])}")

        list
    }

    private fun parameterIndexIsRequired(index: Int) = index + 1 <= minArgs

    init {
        require(isParent || isExecutable) {
            "Command '$name' is empty - a command should contain children, a handler, or both"
        }
    }

    fun isSendableBy(source: Source) = sourceRequirement(source, this)

    fun showHelp(source: Source) = helpFn(source, this).forEach { source.print(it) }

    fun isRequired(parameter: Parameter) = parameterIndexIsRequired(parameters.indexOf(parameter))


}