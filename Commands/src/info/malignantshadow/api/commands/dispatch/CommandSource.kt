package info.malignantshadow.api.commands.dispatch

open class CommandSource {

    companion object {

        @JvmStatic
        fun hasPermission(permission: String) = {
            source: CommandSource -> source.hasPermission(permission)
        }

    }

    open fun hasPermission(permission: String) = true
    open fun print(message: String, vararg args: Any) = println(message.format(*args))
    open fun printErr(message: String, vararg args: Any) = System.err.println(message.format(*args))

}