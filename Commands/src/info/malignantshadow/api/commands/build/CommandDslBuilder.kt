package info.malignantshadow.api.commands.build

@CommandDsl
abstract class CommandDslBuilder<out T> {

    internal abstract fun build(): T

}