package info.malignantshadow.api.util.random

open class Pattern<T>(val input: String?, val transform: (String?) -> T) : WeightedRandom<T>() {

    init {
        if(input != null) {
            val split = input.split(",")
            for (s in split) {
                if (s.matches(Regex("[0-9]+%.+"))) {
                    val chance = s.substring(0 until s.indexOf('%')).toInt()
                    add(transform(s.substring(s.indexOf('%') + 1 until s.length)), chance)
                } else add(transform(s))
            }
        }
    }

}