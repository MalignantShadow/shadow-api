package info.malignantshadow.api.commands.build.lists

import info.malignantshadow.api.commands.build.CommandDsl
import info.malignantshadow.api.commands.build.CommandDslBuilder
import info.malignantshadow.api.util.build

@CommandDsl
abstract class CommandDslListBuilder<out B: CommandDslBuilder<T>, T> {

    internal val list =  ArrayList<T>()

    internal abstract fun createBuilder(name: String): B

    infix fun String.has(init: B.() -> Unit): T {
        val v = build(createBuilder(this), init).build()
        list.add(v)
        return v
    }

}