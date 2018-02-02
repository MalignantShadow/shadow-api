package info.malignantshadow.api.commands.build.attributes

interface Aliasable {

    fun alias(alias: String)

    fun aliases(aliases: Iterable<String>) =
            aliases.forEach { alias(it) }

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