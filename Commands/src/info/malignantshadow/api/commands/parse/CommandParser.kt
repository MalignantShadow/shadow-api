package info.malignantshadow.api.commands.parse

import info.malignantshadow.api.commands.CommandParameter
import info.malignantshadow.api.commands.CommandSpec
import info.malignantshadow.api.commands.build.CommandParameterBuilder
import info.malignantshadow.api.util.parsing.Tokenizer

/**
 * Represents an object that parses command input
 *
 * @param cmd The command whose input is being parsed
 * @param input The input to the command
 */
class CommandParser(val cmd: CommandSpec, val input: String) {

    companion object {

        /**
         * Represents a string contained in quotes. Used by the
         * [Tokenizer] for identification of tokens.
         */
        const val QUOTED_STRING = 0

        /**
         * Represents a long flag ('--flag'). Used by the
         * [Tokenizer] for identification of tokens.
         */
        const val LONG_FLAG = 1

        /**
         * Represents a short flag ('-f'). Used by the
         * [Tokenizer] for identification of tokens.
         */

        const val SHORT_FLAG = 2

        /**
         * Represents everything else. Used by the
         * [Tokenizer] for identification of tokens.
         */
        const val OTHER = 4

        fun getTokenizer(input: String): Tokenizer {
            val tokenizer = Tokenizer(input)
            val stringRegex = Tokenizer.string()
            tokenizer.addTokenType(stringRegex, QUOTED_STRING)
            tokenizer.addTokenType("--[a-zA-Z-]+=?", LONG_FLAG)
            tokenizer.addTokenType("-[a-zA-Z]+(?=\\s|\$)", SHORT_FLAG)
            tokenizer.addTokenType("\\S+", OTHER)
            return tokenizer
        }

    }

    private val tokenizer = getTokenizer(input)

    /**
     * The parsed elements. Parsing takes places as soon as the value of this property
     * is called upon.
     */
    val elements: List<CommandElement> by lazy {
        val elements = ArrayList<CommandElement>()

        var token = tokenizer.next()
        val unnamed = ArrayList<String>()
        while (token != null) {
            when (token.type) {
                QUOTED_STRING -> unnamed.add(token.match.trimEnds())
                OTHER -> unnamed.add(token.match)
                SHORT_FLAG -> {
                    if (token.match.length == 2) {
                        val param = getParam(token.match)
                        if (param.needsValue)  //next token (if string or other) = value
                            addElement(elements, tokenizer, param)
                        else
                            elements.add(CommandElement(param, ""))
                    } else token.match.substring(1).forEach {
                        // i.e -afln
                        addElement(elements, getParam("-$it"))
                    }
                }
                LONG_FLAG -> { // --flag=value or --flag
                    if (token.match.endsWith("=")) { // next token = value
                        val param = getParam(token.match.trimEnds()) // removes first - and ending =
                        if (!param.needsValue)
                            throw CommandParseException(cmd, "Flag '${param.fullName}' does not accept a value")
                        addElement(elements, tokenizer, param, false)
                    } else {
                        val name = token.match.substring(1)
                        addElement(elements, getParam(name))
                    }
                }
            }
            token = tokenizer.next()
        }

        // check if any flags are missing
        cmd.flags.forEach { f ->
            // ignore isRequired prop if the flag does not accept a value anyway
            val required = f.needsValue && f.isRequired
            if (required && elements.firstOrNull { it.key!!.name == f.name } == null) // flag is required and missing
                throw CommandParseException(cmd, "Missing flag and value: '${f.fullName}'")
        }

        // parse unnamed parameters (those that aren't flags)
        val unnamedParams = cmd.params.filter { !it.isFlag }
        val min = cmd.minArgs
        if (unnamed.size < min)
            throw CommandParseException(cmd, "Expected at least $min non-flag arguments, received ${unnamed.size}")
        val argElements = ArrayList<CommandElement>()
        var optionalLeft = unnamed.size - min
        var index = 0
        unnamedParams.forEach {
            val add = when {
                it.isRequired -> true
                optionalLeft > 0 -> {
                    optionalLeft--
                    true
                }
                else -> { // add the default by saying the input is empty
                    argElements.add(CommandElement(it, ""))
                    false
                }
            }
            if (add)
                argElements.add(CommandElement(it, unnamed[index++]))
        }

        if (index <= unnamed.lastIndex)
            for (i in index..unnamed.lastIndex)
                argElements.add(CommandElement(null, unnamed[i]))

        elements.addAll(0, argElements)
        elements
    }

    private fun requiresValue(param: CommandParameter): Nothing =
            throw CommandParseException(cmd, "Flag '${param.fullName}' requires a value")

    private fun addElement(elements: ArrayList<CommandElement>, tokenizer: Tokenizer, param: CommandParameter, skipWhitespace: Boolean = true) {
        val token = tokenizer.next(skipWhitespace)
        when (token?.type) {
            null, LONG_FLAG, SHORT_FLAG -> requiresValue(param)
            OTHER -> elements.add(CommandElement(param, token.match))
            QUOTED_STRING -> elements.add(CommandElement(param, token.match.trimEnds()))
        }
    }

    private fun addElement(elements: ArrayList<CommandElement>, param: CommandParameter) {
        if (param.needsValue) requiresValue(param)
        elements.add(CommandElement(param, ""))
    }

    private val CommandParameter.needsValue get() = !types.isEmpty()

    private fun String.trimEnds() = this.substring(1 until lastIndex)

    private fun getParam(name: String) =
            cmd.params.firstOrNull { it.name == name }
                    ?: CommandParameterBuilder(name, "", "").build()


}