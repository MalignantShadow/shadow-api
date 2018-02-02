package info.malignantshadow.api.commands.build.lists

import info.malignantshadow.api.commands.Flag
import info.malignantshadow.api.commands.build.FlagBuilder
import info.malignantshadow.api.util.build

class FlagListBuilder: CommandDslListBuilder<FlagBuilder, Flag>() {

    override fun createBuilder(name: String) = FlagBuilder(name)

}

fun flags(init: FlagListBuilder.() -> Unit) =
        build(FlagListBuilder(), init).list