package info.malignantshadow.api.commands.build.attributes

interface Aliasable {

    /**
     * Adds an alias to this object
     * @param alias The alias to add
     */
    fun alias(alias: String)

    /**
     * Adds multiple aliases to this object
     * @param aliases The aliases to add
     */
    fun aliases(aliases: Iterable<String>) =
            aliases.forEach { alias(it) }

    /**
     * Adds multiple aliases to this object
     * @param first The first alias
     * @param second The second alias
     * @param others Other aliases to add
     */
    fun aliases(first: String, second: String, vararg others: String) =
            aliases(listOf(first, second, *others))

}

class SimpleAliasable : Aliasable {

    val aliases = ArrayList<String>()

    fun checkAlias(alias: String) {
        check(!alias.matches(Regex("^[\"'\\-]"))) {
            "Alias cannot start with a dash or quoting character (given: $alias)"
        }
        check(!alias.contains(Regex("\\s+"))) { "Alias cannot contain whitespace (given: $alias)" }
    }

    override fun alias(alias: String) {
        checkAlias(alias)
        require(aliases.none { it.equals(alias, true) }) { "Duplicate alias '$alias'" }

        aliases.add(alias)
    }

}