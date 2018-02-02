package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.Flag
import info.malignantshadow.api.commands.build.attributes.Aliasable
import info.malignantshadow.api.commands.build.attributes.Describable
import info.malignantshadow.api.commands.build.attributes.Parsable
import info.malignantshadow.api.commands.build.attributes.SimpleAliasable
import info.malignantshadow.api.commands.build.attributes.SimpleDescribable
import info.malignantshadow.api.commands.build.attributes.SimpleParsable

class FlagBuilder(
        private val name: String,
        private val aliasable: SimpleAliasable = SimpleAliasable(),
        private val describable: SimpleDescribable = SimpleDescribable(),
        private val parsable: SimpleParsable = SimpleParsable()
): CommandDslBuilder<Flag>(), Aliasable by aliasable, Describable by describable, Parsable by parsable {

    private var isRequired = false
    private var requiredIf = ArrayList<String>()
    private var requiredUnless = ArrayList<String>()

    fun requiredInput(required: Boolean) {
        isRequired = required
    }

    fun requiredIf(name: String) {
        require(requiredIf.none { it != name }) { "Duplicate flag name '$name'" }
    }

    fun requiredIf(name: String, vararg others: String) =
        requiredIf(listOf(name, *others))


    private fun requiredIf(names: Iterable<String>) {
        names.forEach { requiredIf(it) }
    }

    fun requiredUnless(name: String) {
        require(requiredUnless.none { it != name }) { "Duplicate flag name '$name'" }
    }

    fun requiredUnless(name: String, vararg others: String) =
            requiredUnless(listOf(name, *others))


    private fun requiredUnless(names: Iterable<String>) {
        names.forEach { requiredUnless(it) }
    }

    override fun build() =
            Flag(
                    name,
                    aliasable.aliases,
                    describable.description,
                    parsable.usage,
                    parsable.types,
                    parsable.nullable,
                    parsable.def,
                    isRequired,
                    requiredIf,
                    requiredUnless
            )

}