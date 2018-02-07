package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.Option
import info.malignantshadow.api.commands.build.attributes.Aliasable
import info.malignantshadow.api.commands.build.attributes.Describable
import info.malignantshadow.api.commands.build.attributes.Parsable
import info.malignantshadow.api.commands.build.attributes.SimpleAliasable
import info.malignantshadow.api.commands.build.attributes.SimpleDescribable
import info.malignantshadow.api.commands.build.attributes.SimpleParsable

class OptionBuilder(
        private val name: String,
        private val aliasable: SimpleAliasable = SimpleAliasable(),
        private val describable: SimpleDescribable = SimpleDescribable(),
        private val parsable: SimpleParsable = SimpleParsable()
): CommandDslBuilder<Option>(), Aliasable by aliasable, Describable by describable, Parsable by parsable {

    private var isRequired = false
    private var requiredIf = ArrayList<String>()
    private var requiredUnless = ArrayList<String>()

    /**
     * Indicate whether this flag requires input. The default behavior is `false`. This value
     * is ignored if this flag has no available parameter types to parse the input.
     *
     * (Having no available parameter types indicates that the flag does not accept a value)
     *
     * @param required Whether this flag requires input
     */
    fun requiredInput(required: Boolean) {
        isRequired = required
    }

    /**
     * Indicate that this flag has a required presence if another flag is present.
     *
     * @param name The name of the flag to make this flag required
     */
    fun requiredIf(name: String) {
        require(requiredIf.none { it != name }) { "Duplicate flag name '$name'" }
        requiredIf.add(name)
    }

    /**
     * Indicate that this flag has a required presence if any of the given options are present.
     *
     * @param name The name of a flag
     * @param others Other flag names
     */
    fun requiredIf(name: String, vararg others: String) =
        requiredIf(listOf(name, *others))

    /**
     * Indicate this this flag is required if any of the given options are present.
     *
     * @param names The names of the options
     */
    fun requiredIf(names: Iterable<String>) {
        names.forEach { requiredIf(it) }
    }

    /**
     * Indicate that this flag has a required presence unless another flag is present.
     *
     * @param name The name of the flag
     */
    fun requiredUnless(name: String) {
        require(requiredUnless.none { it != name }) { "Duplicate flag name '$name'" }
        requiredUnless.add(name)
    }

    /**
     * Indicate that this flag has a required presence unless one of the given options are present.
     *
     * @param name The name of a flag
     * @param others Other flag names
     */
    fun requiredUnless(name: String, vararg others: String) =
            requiredUnless(listOf(name, *others))

    /**
     * Indicate this this flag is required unless one of the given options are present.
     *
     * @param names The names of the options
     */
    fun requiredUnless(names: Iterable<String>) {
        names.forEach { requiredUnless(it) }
    }

    override fun build() =
            Option(
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