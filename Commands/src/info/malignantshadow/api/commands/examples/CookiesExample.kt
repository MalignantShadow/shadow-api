package info.malignantshadow.api.commands.examples

import info.malignantshadow.api.commands.CommandError
import info.malignantshadow.api.commands.CommandResult
import info.malignantshadow.api.commands.CommandSender
import info.malignantshadow.api.commands.simple.SimpleCommandContext
import info.malignantshadow.api.commands.simple.commandManager
import info.malignantshadow.api.util.max
import info.malignantshadow.api.util.parsing.ParameterType
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

const val COOKIES_EATEN = 0
const val COOKIES_BAKED = 1
const val COOKIES_CHECKED = 2

data class CookieCommandResult(val type: Int, val cookieType: CookieType?, val amount: Int) : CommandResult

fun eatCookie(ctx: SimpleCommandContext): CookieCommandResult {
    val amount = ctx["amount"] as Int
    val type = ctx["type"] as CookieType
    CookieInventory.remove(type, amount)
    ctx.sender.print("Ate %d ${type.getName()} cookie%s. Yummy!", amount, if (amount == 1) "" else "s")
    return CookieCommandResult(COOKIES_EATEN, type, amount)
}

val me = CommandSender()
val cookieManager = commandManager {
    command("cookie", "Commands related to cookies") {
        alias("c")
        val bake = command("bake", "Bake cookies") {
            param("type", "The type of cookie to eat") {
                isRequired()
                typeOf<CookieType>()
            }
            param("amount", "The amount of cookies") {
                typeOf(ParameterType.INT)
                def = 1
            }
            // handler can be an anonymous function
            handler = { ctx ->
                val amount = ctx["amount"] as Int
                val type = ctx["type"] as CookieType
                CookieInventory.add(type, amount)
                ctx.sender.print("Baked $amount ${type.getName()} cookie%s", if (amount == 1) "" else "s")
                CookieCommandResult(COOKIES_BAKED, type, amount)
            }
        }
        command("eat", "Eat cookies") {
            params(bake.params)
            handler = ::eatCookie // or a function reference
        }
        command("inventory", "Check the inventory") {
            alias("inv")
            param("type", "The type of cookie. Leave blank to see entire inventory") {
                isNullable()
                typeOf<CookieType>()
            }
            handler = handler@ { ctx ->
                val type = ctx["type"] as CookieType?
                if (ctx.isPresent("type") && type == null) {
                    ctx.sender.printErr("Unknown cookie type '${ctx.getInput("type")}'")
                    return@handler CommandError(ctx)
                }

                if (type == null) {
                    var count = 0
                    CookieType.forEach {
                        val amount = CookieInventory[it]
                        ctx.sender.print("${it.getName()} - $amount")
                        count += amount
                    }
                    return@handler CookieCommandResult(COOKIES_CHECKED, null, count)
                }
                val amount = CookieInventory[type]
                ctx.sender.print("$amount cookie%s of type '${type.getName()}'", if (amount == 1) "" else "s")
                return@handler CookieCommandResult(COOKIES_CHECKED, type, amount)
            }
        }
        helpCommand()
    }
    helpCommand()
}

fun main(args: Array<String>) {
    val joined = args.joinToString(separator = " ")
    for (cmd in joined.split(Regex(";\\s+")))
        cookieManager.dispatch(me, cmd)
}