package info.malignantshadow.api.commands

import info.malignantshadow.api.commands.dispatch.Context
import info.malignantshadow.api.commands.dispatch.Result
import info.malignantshadow.api.commands.dispatch.Source
import info.malignantshadow.api.commands.parse.CommandInput
import info.malignantshadow.api.commands.parse.CommandParser
import info.malignantshadow.api.util.wrap

class Manager(
        children: List<Command>,
        val beforeDispatch: (Context) -> Boolean,
        val afterDispatch: (Context, Any?) -> Unit,
        val sourceRequirementFallthrough: Boolean,
        val ignoreUnnecessaryFlagInput: Boolean
) : CommandContainer(children) {

    companion object {

        //dispatch error
        const val MISSING_FLAG = 0
        const val FLAG_DOES_NOT_ACCEPT_VALUE = 1
        const val FLAG_MISSING_VALUE = 2
        const val NOT_ENOUGH_ARGS = 3
        const val INVALID_INPUT = 4
        const val EXCEPTION_DURING_DISPATCH = 5
        const val CMD_REQUIRES_SUB = 6

        //search error
        const val INVALID_CMD_NAME = 0
        const val CMD_NOT_FOUND = 1

        val DEFAULT_HELP_FUNCTION = configuredHelpFn(7, 77)

        fun configuredHelpFn(separatorLength: Int, maxLineLength: Int) = { cmd: Command ->
            // GNU-like command help

            // Command description
            //
            // name <required> [optional] $extraArgUsage? [flags/options]
            // name <sub_command>
            // name --help
            //
            // Commands:
            // name                   description
            // longerName             description
            //
            // Options:
            // --longer=<value>       description
            // -m <message>           description
            // -s, --alias            description
            // -t, --type=TYPE        description
            //                        (Required)
            //                        (Required if ... is present)
            //                        (Required unless ... is present)
            // -h, --help              Show this help message and then exit

            // helper function to produce output like above
            fun helpLines(header: String, padLength: Int, description: String, maxLineLength: Int): List<String> {
                val help = ArrayList<String>()
                val wrappedDescription = description.wrap(maxLineLength - padLength)
                help.add(header.padEnd(padLength) + wrappedDescription[0])
                if (wrappedDescription.size > 1)
                    wrappedDescription
                            .slice(1..wrappedDescription.lastIndex)
                            .forEach { help.add("".padEnd(padLength) + it) }
                return help
            }

            val help = ArrayList<String>()
            help.add(if (cmd.description.isNotBlank()) cmd.description else "This command has no description.")
            help.add("")
            help.addAll(cmd.usage)

            // determine the padLength by getting the longest
            // header length (command/flag names) and adding separatorLength
            var padLength = 0
            val helpFlagUsage = cmd.helpFlags.joinToString { Flag.getDisplay(it) }
            cmd.children.forEach { padLength = Math.max(padLength, it.joinedAliases.length) }
            cmd.flags.forEach { padLength = Math.max(padLength, it.computedUsage.length) }
            padLength = Math.max(padLength, helpFlagUsage.length)
            padLength += separatorLength

            if (cmd.isParent) {
                help.add("")
                help.add("Commands:")
                cmd.children
                        .sortedBy { it.name }
                        .forEach { help.addAll(helpLines(it.joinedAliases, padLength, it.description, maxLineLength)) }
            }

            if (cmd.hasFlags) {
                help.add("")
                help.add("Options:")
                help.add("Mandatory arguments to long options are mandatory to short options too.")
                cmd.flags.sortedBy { it.name }.forEach {
                    help.addAll(helpLines(it.computedUsage, padLength, it.description, maxLineLength))

                    // Note - you shouldn't mix required, requiredIf, or requiredUnless anyway
                    when {
                        it.isRequired -> help.add("".padEnd(padLength) + "(Required)")
                        it.requiredIf.isNotEmpty() ->
                            help.addAll(helpLines("", padLength, "(Required if any of the following are present: ${it.requiredIf.joinToString()})", maxLineLength))
                        it.requiredUnless.isNotEmpty() ->
                            help.addAll(helpLines("", padLength, "(Required if any of the following are present: ${it.requiredUnless.joinToString()})", maxLineLength))
                    }
                }

                if (cmd.helpFlags.isNotEmpty())
                    help.addAll(helpLines(helpFlagUsage, padLength, "Show this help message and then exit", maxLineLength))
            }

            help
        }

    }

    data class DispatchErrorResult(val type: Int, val source: Source, val cmd: Command, val args: List<CommandInput>)

    data class CommandSearchErrorResult(val type: Int, val source: Source, val commandLine: String, val token: String)

    data class RequirementNotMetResult(val source: Source, val cmd: Command)

    data class CommandNotDispatchedResult(val context: Context)

    data class HelpShownResult(val source: Source, val cmd: Command, val flag: String)

    private fun reqNotMeet(source: Source, cmd: Command): RequirementNotMetResult {
        source.printErr("You are not allowed to run this command")
        return RequirementNotMetResult(source, cmd)
    }

    private fun notFound(source: Source, command: String, name: String): CommandSearchErrorResult {
        source.printErr("Could not find a command with an alias of '%s'", name)
        return CommandSearchErrorResult(CMD_NOT_FOUND, source, command, name)
    }

    fun dispatch(source: Source, command: String): Any? {
        require(!command.isBlank()) { "Command string cannot be blank" }
        val tokenizer = CommandParser.getTokenizer(command)
        var token = tokenizer.next() // There should be a token if the string isn't blank
        if (token!!.type != CommandParser.OTHER) {
            source.printErr("Expected a command name, received: %s", token.match)
            return CommandSearchErrorResult(INVALID_CMD_NAME, source, command, token.match)
        }

        var cmd = children.firstOrNull { it.hasAlias(token!!.match) } ?: return notFound(source, command, token.match)
        while (true) {
            if (sourceRequirementFallthrough && !cmd.isSendableBy(source)) reqNotMeet(source, cmd)

            if (!cmd.isParent) // cmd has no children, therefore has a handler
                return checkAndDispatch(!sourceRequirementFallthrough, source, cmd, tokenizer.rest)

            token = tokenizer.next()
            if (token == null || token.type != CommandParser.OTHER)
                return dispatch(source, cmd, "${token?.match ?: ""} ${tokenizer.rest}")

            val newCmd = cmd.children.firstOrNull { it.hasAlias(token.match) }
            if (newCmd == null)
                return checkAndDispatch(!sourceRequirementFallthrough, source, cmd, "${token.match} ${tokenizer.rest}")
            else
                cmd = newCmd
        }
    }

    private fun checkAndDispatch(checkRequirement: Boolean, source: Source, cmd: Command, args: String): Any? {
        if (checkRequirement && !cmd.isSendableBy(source)) return reqNotMeet(source, cmd)
        return dispatch(source, cmd, args)
    }

    // DOCS: "dispatch as if the given command was part of this manager, does not check permission"
    fun dispatch(source: Source, cmd: Command, args: String) = dispatch(source, cmd, CommandParser.parse(cmd, args))

    // DOCS: "dispatch as if the given command was part of this manager, does not check permission"
    fun dispatch(source: Source, cmd: Command, args: List<CommandInput>): Result? {
        // if a help flag is present, show help for that command
        val flagInputs = args.filter { it.key != null && it.key is Flag }
        val flagsMapped = flagInputs.map { it.key as Flag }
        val helpFlagName = cmd.helpFlags.firstOrNull { name -> flagInputs.any { (it.key as Flag).hasAlias(name) } }
        if (helpFlagName != null) {
            cmd.showHelp(source)
            return HelpShownResult(source, cmd, helpFlagName)
        }

        if (!cmd.isExecutable) {
            source.printErr("This command requires a sub-command")
            return DispatchErrorResult(CMD_REQUIRES_SUB, source, cmd, args)
        }

        // test arg count
        // this includes keys which are null, so extra arguments are included as well
        val paramInputs = args.filter { it.key !is Flag}
        val argCount = paramInputs.count {it.input != null}
        if (argCount < cmd.minArgs) {
            source.printErr("Expected %d arguments, but received %d", cmd.minArgs, argCount)
            return DispatchErrorResult(NOT_ENOUGH_ARGS, source, cmd, args)
        }

        // flag is required but not found in 'args'
        cmd.flags.forEach { f ->
            if (f.isRequired(flagsMapped) && flagInputs.none { !(it.key as Flag).aliases.any { f.hasAlias(it) } }) {
                source.printErr("Missing required flag: '%s'", f.shownDisplay)
                return DispatchErrorResult(MISSING_FLAG, source, cmd, args)
            }
        }

        // flag was given input when not needed
        if (!ignoreUnnecessaryFlagInput) {
            val badFlag = flagInputs.firstOrNull { (it.key as Flag).requiresPresenceOnly && it.input != null }
            if (badFlag != null) {
                source.printErr("Flag '%s' does not accept a value", badFlag.key!!.shownDisplay)
                return DispatchErrorResult(FLAG_DOES_NOT_ACCEPT_VALUE, source, cmd, args)
            }
        }

        //flag is missing input
        val missingValue = flagInputs.firstOrNull {
            (it.key as Flag).isRequired(flagsMapped)
                    && !it.key.nullable
                    && it.input == null
        }
        if (missingValue != null) {
            source.printErr("Flag '%s' requires a value, but was not given one", missingValue.key!!.name)
            return DispatchErrorResult(FLAG_MISSING_VALUE, source, cmd, args)
        }

        // test valid inputs for parameters
        val failedParam = paramInputs.firstOrNull {
            // ignore 'extra' arguments, because they do not have an associated type
            val param = it.key ?: return@firstOrNull false
            cmd.isRequired(param) && it.value == null && !param.nullable
        }
        if (failedParam != null) {
            source.printErr("Invalid input for parameter '%s' - %s", failedParam.key!!.name, failedParam.input)
            return DispatchErrorResult(INVALID_INPUT, source, cmd, args)
        }

        // test valid inputs for flags
        val failedFlag = flagInputs.firstOrNull {
            val flag = it.key as Flag
            flag.isRequired && it.value == null && !flag.nullable
        }
        if (failedFlag != null) {
            source.printErr("Invalid input for flag '%s' - %s", failedFlag.key!!.shownDisplay, failedFlag.input)
            return DispatchErrorResult(INVALID_INPUT, source, cmd, args)
        }

        val context = Context(source, cmd, args)
        if (beforeDispatch(context)) {
            val result = dispatchUnsafe(context)
            afterDispatch(context, result)
            return result
        }
        return CommandNotDispatchedResult(context)
    }

    fun dispatchUnsafe(source: Source, cmd: Command, args: List<CommandInput>): Any? = dispatchUnsafe(Context(source, cmd, args))

    private fun dispatchUnsafe(context: Context): Any? {
        return try {
            Command.dispatchContext(context)
        } catch (e: Exception) {
            DispatchErrorResult(EXCEPTION_DURING_DISPATCH, context.source, context.cmd, context.givenArgs)
        }
    }
}