package info.malignantshadow.api.commands

open class CommandSender {

    fun print(o: Any?) = print(o.toString())
    open fun print(message: String, vararg args: Any?) = println(message.format(*args))

    fun printErr(o: Any?) = printErr(o.toString())
    open fun printErr(message: String, vararg args: Any?) = println(message.format(*args))

}