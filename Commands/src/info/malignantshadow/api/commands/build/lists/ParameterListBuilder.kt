package info.malignantshadow.api.commands.build.lists

import info.malignantshadow.api.commands.Parameter
import info.malignantshadow.api.commands.build.ParameterBuilder
import info.malignantshadow.api.util.build

class ParameterListBuilder: CommandDslListBuilder<ParameterBuilder, Parameter>() {

    override fun createBuilder(name: String) = ParameterBuilder(name)

}

/**
 * Creates a list of parameters.
 *
 * @param init Te extension function used to build the list
 */
fun parameters(init: ParameterListBuilder.() -> Unit) =
        build(ParameterListBuilder(), init).list