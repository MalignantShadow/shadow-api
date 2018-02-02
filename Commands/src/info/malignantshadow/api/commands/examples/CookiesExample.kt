package info.malignantshadow.api.commands.examples

import info.malignantshadow.api.commands.build.lists.commands
import info.malignantshadow.api.commands.build.lists.parameters
import info.malignantshadow.api.commands.dispatch.Result
import info.malignantshadow.api.commands.dispatch.Source
import info.malignantshadow.api.util.parsing.ParameterTypes
import info.malignantshadow.api.util.toProperCase

enum class CookieType {

    CHOCOLATE_CHIP,
    OATMEAL,
    SNICKERDOODLE,
    SUGAR;

    fun getName() = name.replace("_", " ").toProperCase()

}

object CookieInventory {

    private val inv = HashMap<CookieType, Int>()

    operator fun get(type: CookieType) = inv[type] ?: 0

    fun add(type: CookieType, amount: Int) {
        inv[type] = inv.getOrDefault(type, 0) + Math.max(0, amount)
    }

    fun remove(type: CookieType, amount: Int) {
        inv[type] = inv.getOrDefault(type, 0) - Math.max(0, amount)
    }

}

class CookiesExample {

    data class CookieCommandResult(val cmdName: String, val type: CookieType?, val amount: Int) : Result

    private val cookieTypeAndAmount = parameters {
        "type" has {
            type(ParameterTypes.enumValue<CookieType>())
        }
        "amount" has {
            type(ParameterTypes.UNSIGNED_INT) // can only be positive
            defaultValue(1)
        }
    }

    val manager = commands {
        "cookie" has {
            description("Cookie commands")
            "eat" has {
                minimumArgumentCountOf(1)
                description("Eat some cookies")
                parameters(cookieTypeAndAmount)
                handler {
                    val type = it["type"] as CookieType
                    val amount = Math.min(CookieInventory[type], it["amount"] as Int)
                    if(amount == 0) {
                        it.source.printErr("Amount cannot be 0")
                        return@handler null
                    }
                    CookieInventory.remove(type, amount)
                    it.source.print("Ate $amount ${type.getName()} cookie${if (amount == 1) "" else "s"}. They were delicious!")
                    CookieCommandResult("bake", type, amount)
                }
                helpFlags()
            }
            "bake" has {
                minimumArgumentCountOf(1)
                description("Bake some cookies")
                parameters(cookieTypeAndAmount)
                handler {
                    val type = it["type"] as CookieType
                    val amount = it["amount"] as Int
                    CookieInventory.add(type, amount)
                    it.source.print("Baked $amount ${type.getName()} cookie${if (amount == 1) "" else "s"}")
                    CookieCommandResult("bake", type, amount)
                }
                helpFlags()
            }
            "inventory" has {
                alias("inv")
                description("Check the inventory")
                parameter("type") {
                    type(ParameterTypes.enumValue<CookieType>())
                    nullableValue()
                }
                handler {
                    val type = it["type"] as? CookieType
                    var totalAmount = 0
                    if(type == null) {
                        CookieType.values().forEach { t ->
                            val amount = CookieInventory[t]
                            if(amount > 0) {
                                it.source.print("${t.getName()} - $amount")
                                totalAmount += amount
                            }
                        }
                    } else {
                        totalAmount = CookieInventory[type]
                        it.source.print("${type.getName()} - $totalAmount")
                    }
                    CookieCommandResult("inventory", type, totalAmount)
                }
                helpFlags()
            }
            helpFlags()
        }
    }

}

fun main(args: Array<String>) {
    CookiesExample().manager.dispatch(Source(), args.joinToString(" "))
}