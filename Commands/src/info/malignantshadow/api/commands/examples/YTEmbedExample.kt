package info.malignantshadow.api.commands.examples

import info.malignantshadow.api.commands.build.lists.commands
import info.malignantshadow.api.util.parsing.ParameterTypes

object YTEmbedExample {

    val manager = commands {
        "ytembed" has {
            val intBoolean = ParameterTypes.choices(0, 1)
            parameter("video-id")
            option("autoplay") {
                alias("a")
                type(intBoolean)
            }
            option("cc_load_policy") {
                alias("cc")
            }
            option("color", ParameterTypes.choices("red", "white"))
            option("disablekb", intBoolean)
            option("enablejsapi") {
                alias("js")
                type(intBoolean)
            }
            option("end") {
                alias("e")
                type(ParameterTypes.UNSIGNED_INT)
            }
            option("fs") {
                alias("fullscreen")
                type(intBoolean)
            }
            option("hl", ParameterTypes.matchesPattern("([a-z]{2}|[a-zA-Z]{2}-[a-zA-Z]{2})"))
            option("iv_load_policy") {
                alias("iv")
                // Kinda confusing, see https://developers.google.com/youtube/player_parameters#iv_load_policy
                type(ParameterTypes.choices(1, 3))
            }
            option("list")
            option("listType") {
                alias("lt")
                type(ParameterTypes.choices("playlist", "search", "user_uploads"))
            }
            option("loop", intBoolean)
            option("modestbranding") {
                alias("m")
                type(intBoolean)
            }
            option("origin") {
                alias("o")
            }
            option("playlist") {
                alias("p")
                type(ParameterTypes.listOf(ParameterTypes.matchesPattern("(\\w|-){11}")))
            }
            option("playsinline", intBoolean)
            option("rel", intBoolean)
            option("showinfo", intBoolean)
            option("start") {
                alias("s")
                type(ParameterTypes.UNSIGNED_INT)
            }
            option("widget_referrer")
        }
    }

}

fun main(args: Array<String>) {

}