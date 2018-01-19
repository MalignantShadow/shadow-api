package info.malignantshadow.api.commands.examples

import info.malignantshadow.api.commands.build.commandManager
import info.malignantshadow.api.commands.dispatch.CommandResult
import info.malignantshadow.api.commands.dispatch.CommandSource
import info.malignantshadow.api.util.parsing.ParameterType

enum class CookieType {

    CHCOCOLATE_CHIP,
    OATMEAL,
    SNICKERDOODLE,
    SUGAR;



}

data class CookieCommandResult(val type: Int, val amount: Int) : CommandResult

object CookieInventory {

}

val manager = commandManager {
    command("cookie", "CookieCommand") {
        val bake = child("bake", "Bake a cookie"){
            param("amount", "The amount of cookies") {
                typeOf(ParameterType.INT)
                def(1)
            }
            param("type", "The type of cookie") {
                isRequired()
                typeOf(ParameterType.enumValue<CookieType>())
            }
            handler {
                CookieCommandResult(0, 0)
            }
        }
        child("eat", "Eat a command") {
            params(bake.params)
            handler {
                CookieCommandResult(0, 0)
            }
        }
        child("inv", "Check the inventory") {
            handler {
                CookieCommandResult(0, 0)
            }
        }
        child("test", "Test Command") {
            param("-f", "A flag") {
                typeOf(ParameterType.STRING)
            }
            param("value", "A value") {
                def("")
            }
            handler {
                it.source.print("Input:")
                it.source.print("-------------")
                it.args.forEach { arg ->
                    it.source.print("${arg.key?.name}(${arg.input}) = ${arg.value}")
                }
                if(it.hasExtra)
                it.source.print("extra: ${it.extra.joinToString()}")

                it.source.print("")

                it.source.print("From Spec:")
                it.source.print("-------------")
                it.cmd.params.forEach { param ->
                    it.source.print("${param.fullName} isPresent: ${it.contains(param.name)}, value = " + it[param.name])
                }

                null
            }
        }
    }
}

val me = CommandSource()

fun main(args: Array<String>) {
    println("command result: " + manager.dispatch(me, "cookie test -f \"sudbfiu sdu bsdufbs iusikdf b\""))
}