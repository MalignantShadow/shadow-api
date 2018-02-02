package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.Parameter
import info.malignantshadow.api.commands.build.attributes.Describable
import info.malignantshadow.api.commands.build.attributes.Parsable
import info.malignantshadow.api.commands.build.attributes.SimpleDescribable
import info.malignantshadow.api.commands.build.attributes.SimpleParsable
import info.malignantshadow.api.util.parsing.ParameterTypes

class ParameterBuilder(
        private val name: String,
        private val describable: SimpleDescribable = SimpleDescribable(),
        private val parsable: SimpleParsable = SimpleParsable()
): CommandDslBuilder<Parameter>(), Describable by describable, Parsable by parsable {

    private var display = ""

    override fun build() =
            Parameter(
                    name,
                    describable.description,
                    parsable.usage,
                    if(parsable.types.isEmpty()) listOf(ParameterTypes.STRING) else parsable.types,
                    parsable.nullable,
                    parsable.def
            )


}