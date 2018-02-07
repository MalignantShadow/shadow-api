package info.malignantshadow.api.commands.build.lists

import info.malignantshadow.api.commands.Option
import info.malignantshadow.api.commands.build.OptionBuilder
import info.malignantshadow.api.util.build

class OptionListBuilder: CommandDslListBuilder<OptionBuilder, Option>() {

    override fun createBuilder(name: String) = OptionBuilder(name)

}

/**
 * Creates a list of options
 */
fun options(init: OptionListBuilder.() -> Unit): List<Option> =
        build(OptionListBuilder(), init).list