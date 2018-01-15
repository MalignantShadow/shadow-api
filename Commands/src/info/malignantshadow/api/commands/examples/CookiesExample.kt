package info.malignantshadow.api.commands.examples

import info.malignantshadow.api.commands.CommandSender
import info.malignantshadow.api.commands.simple.SimpleCommandContext
import info.malignantshadow.api.commands.simple.commandManager
import info.malignantshadow.api.util.max
import info.malignantshadow.api.util.parsing.StringTransformers
import info.malignantshadow.api.util.toProperCase

enum class CookieType {

    CHOCOLATE_CHIP,
    M_AND_M,
    OREO,
    SNICKERDOODLE,
    SUGAR;

    companion object : Iterable<CookieType> {

        override fun iterator(): Iterator<CookieType> = CookieType.values().iterator()

    }

    fun getName() = super.name.replace("_", " ").toProperCase()

}

object CookieInventory {

    private val inv = HashMap<CookieType, Int>()

    fun add(type: CookieType, amount: Int) {
        inv[type] = get(type) + amount.max(0)
    }

    fun remove(type: CookieType, amount: Int) {
        inv[type] = Math.max(get(type) - amount, 0)
    }

    operator fun get(type: CookieType) = inv[type] ?: 0

}

fun eatCookie(ctx: SimpleCommandContext) {
    val amount = ctx["amount"] as Int
    CookieInventory.remove(ctx["type"] as CookieType, amount)
    ctx.sender.print("Ate %d cookie%s", amount, if (amount == 1) "" else "s")
    ctx.sender.print("Yummy!")
}

val me = CommandSender()
val cookieManager = commandManager {
    command("cookie", "Commands related to cookies") {
        alias("c")
        val bake = command("bake", "Bake cookies") {
            arg("type", "The type of cookie to eat") {
                isRequired()
                typeOf<CookieType>()
            }
            arg("amount", "The amount of cookies") {
                typeOf(StringTransformers.INT)
                def = 1
            }
            // handler can be an anonymous function
            handler = { ctx ->
                val amount = ctx["amount"] as Int
                CookieInventory.add(ctx["type"] as CookieType, amount)
                ctx.sender.print("Baked $amount cookie%s", if (amount == 1) "" else "s")
            }
        }
        command("eat", "Eat cookies") {
            args(bake.args)
            handler = ::eatCookie // or a function reference
        }
        command("inventory", "Check the inventory") {
            alias("inv")
            arg("type", "The type of cookie. Leave blank to see entire inventory") {
                isNullable()
                typeOf<CookieType>()
            }
            handler = { ctx ->
                val type = ctx["type"] as CookieType?
                if (type == null) {
                    for (t in CookieType)
                        ctx.sender.print("${t.getName()} - ${CookieInventory[t]}")
                } else {
                    val amount = CookieInventory[type]
                    ctx.sender.print("$amount cookie%s of type '${type.getName()}'", if (amount == 1) "" else "s")
                }
            }
        }
        helpCommand()
    }
    helpCommand()
}

fun main(args: Array<String>) {
    cookieManager.dispatch(me, "c inv")
//    cookieManager.dispatch(me, args.joinToString(separator = " "))
}