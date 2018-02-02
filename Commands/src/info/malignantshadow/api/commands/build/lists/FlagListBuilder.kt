package info.malignantshadow.api.commands.build.lists

import info.malignantshadow.api.commands.Flag
import info.malignantshadow.api.commands.build.FlagBuilder
import info.malignantshadow.api.util.build

class FlagListBuilder: CommandDslListBuilder<FlagBuilder, Flag>() {

    override fun createBuilder(name: String) = FlagBuilder(name)

}

/**
 * Creates a list of flags
 */
fun flags(init: FlagListBuilder.() -> Unit): List<Flag> =
        build(FlagListBuilder(), init).list