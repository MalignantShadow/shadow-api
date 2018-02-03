package info.malignantshadow.api.commands.parse

import info.malignantshadow.api.commands.Command
import info.malignantshadow.api.commands.Flag
import info.malignantshadow.api.commands.build.FlagBuilder
import info.malignantshadow.api.util.parsing.Tokenizer
import info.malignantshadow.api.util.unescape

/**
 * The helper object used to parse command input. Utilizes [Tokenizer]
 *
 * @author Shad0w (Caleb Downs)
 */
object CommandParser {

    /**
     * Token type denoting a `"quoted string"`
     */
    const val QUOTED_STRING = 0

    /**
     * Token type denoting a short flag list, i.e. `-abcd`
     */
    const val SHORT_FLAG_LIST = 1

    /**
     * Token type denoting a short flag with a potential value, i.e. `-a` or `-a VALUE`
     */
    const val SHORT_FLAG_WITH_VALUE = 2

    /**
     * Token type denoting a long flag with a value, i.e. `--flag=VALUE` or `--flag="VALUE"`
     */
    const val LONG_FLAG_WITH_VALUE = 3

    /**
     * Token type denoting a long flag without a value, i.e. `--flag`
     */
    const val LONG_FLAG = 4

    /**
     * Token type denoting a group of non-whitespace characters that do not match any of
     * the other tokens
     */
    const val OTHER = 5

    private val string = Tokenizer.string()
    private val otherNotFlag = "[^-]\\S*"
    private val flagValue = "($otherNotFlag|$string)"
    private val shortFlag = "-[a-zA-Z]"
    private val longFlag = "--[a-zA-Z-]+"

    /**
     * Returns a new [Tokenizer] to be used in command parsing
     *
     * @param input The arguments to parse
     */
    fun getTokenizer(input: String): Tokenizer {
        val tokenizer = Tokenizer(input)
        tokenizer.addTokenType(Tokenizer.string(), QUOTED_STRING)
        tokenizer.addTokenType("$shortFlag{2,}", SHORT_FLAG_LIST)
        tokenizer.addTokenType("$shortFlag(\\s+$flagValue)?", SHORT_FLAG_WITH_VALUE)
        tokenizer.addTokenType("$longFlag=$flagValue", LONG_FLAG_WITH_VALUE)
        tokenizer.addTokenType(longFlag, LONG_FLAG)
        tokenizer.addTokenType("\\S+", OTHER)
        return tokenizer
    }

    /**
     * Parse the arguments given to a command
     *
     * @param cmd The command being executed
     * @param args The unparsed arguments given to the command
     * @return The parsed arguments
     */
    fun parse(cmd: Command, args: String): List<CommandInput> {
        val tokenizer = getTokenizer(args)

        val unnamed = ArrayList<String>()
        val flags = ArrayList<CommandInput>()
        while (true) {
            val token = tokenizer.next() ?: break
            var testFlags = false
            when (token.type) {
                QUOTED_STRING -> unnamed.add(token.match.substring(1..token.match.lastIndex).unescape())
                OTHER -> unnamed.add(token.match)
                SHORT_FLAG_LIST -> {
                    token.match.substring(1).forEach { flags.add(CommandInput(getFlag(cmd, "$it"), null)) }
                    testFlags = true
                }
                SHORT_FLAG_WITH_VALUE -> {
                    val split = token.match.substring(1).split(Regex("\\s+"), 2)
                    val flag = getFlag(cmd, split[0])
                    val value = split.getOrNull(1)
                    val input =
                            if (value != null && value.matches(Regex(string)))
                                value.substring(1..value.lastIndex)
                            else value
                    if (flag.requiresPresenceOnly) {
                        flags.add(CommandInput(flag, null))
                        if (input != null) unnamed.add(input)
                    } else {
                        flags.add(CommandInput(flag, input))
                    }
                    testFlags = true
                }
                LONG_FLAG_WITH_VALUE -> {
                    val split = token.match.substring(2).split(Regex("="), 2)
                    val value = split[1]
                    val input =
                            if (value.matches(Regex(string)))
                                value.substring(1..value.lastIndex)
                            else value
                    flags.add(CommandInput(getFlag(cmd, split[0]), input))
                    testFlags = true
                }
                LONG_FLAG -> {
                    flags.add(CommandInput(getFlag(cmd, token.match.substring(2)), null))
                    testFlags = true
                }
            }

            // if a help flag is present, stop immediately because the values are unused anyway
            if (testFlags && flags.any { f -> cmd.helpFlags.any { (f.key as Flag).hasAlias(it) } })
                return flags
        }

        val given = cmd.parameters.mapIndexed { index, it -> CommandInput(it, unnamed.getOrNull(index)) }
        val extra =
                if (unnamed.size > given.size)
                    unnamed.slice(given.size..unnamed.lastIndex).map { CommandInput(null, it) }
                else emptyList()

        return given + extra + flags
    }

    private fun getFlag(cmd: Command, name: String) =
            cmd.flags.firstOrNull { it.hasAlias(name) } ?: FlagBuilder(name).build()

}