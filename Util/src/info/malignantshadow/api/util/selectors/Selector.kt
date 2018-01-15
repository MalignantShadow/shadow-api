package info.malignantshadow.api.util.selectors

import info.malignantshadow.api.util.parsing.StringTransformers

open class Selector(val name: String, var args: ArrayList<SelectorArgument>) : Iterable<SelectorArgument> {

    fun add(name: String, vararg input: String) = add(SelectorArgument(name, arrayOf(*input)))
    fun add(arg: SelectorArgument)  {
        val index = args.indexOfFirst { it.name == arg.name }
        if(index == -1) args.add(arg)
        else args[index] = arg
    }

    operator fun get(name: String) = args.firstOrNull { it.name == name }

    fun getInputFor(name: String) = get(name)?.input
    fun hasInputFor(name: String) = getInputFor(name) != null

    fun getAll(name: String) = getAll(name, StringTransformers.STRING)

    @JvmOverloads
    fun <R> getAll(name: String, type: (String?) -> R?, max: Int = -1): ArrayList<R?>? {
        val arg = get(name) ?: return null

        val arr = ArrayList<R?>()
        if(arg.input.isEmpty() || max == 0) return arr

        for((index, str) in arg.input.withIndex()) {
            if(index == max - 1)
                return arr
            arr.add(type(str))
        }
        return arr
    }

    fun getOne(name: String) = getOne(name, StringTransformers.STRING)
    fun <R> getOne(name: String, type: (String?) -> R?) = getAll(name, type, 1)?.getOrNull(0)

    fun isSet(name: String) = get(name) != null

    override fun iterator(): Iterator<SelectorArgument> = args.iterator()

    override fun toString(): String {
        var s = name
        if(args.isEmpty()) return """$name[]"""

        return """$name[${args.joinToString(",")}]"""
    }

    companion object Static {

        val NAME_REGEX = """^[~!@#$%.*?]?\w+"""
        val CONTEXT_REGEX = """$NAME_REGEX(\[[\w=$^~!-|*><,]*])?"""

        fun compile(s: String?): Selector? {
            if(s == null || !s.matches(Regex(CONTEXT_REGEX))) return null

            val name = s.substring(0, if("[" in s) s.indexOf('[') else s.length)
            if(!name.matches(Regex(NAME_REGEX))) return null

            val selector = Selector(name, ArrayList())
            if(name.length == s.length || s.length == name.length + 2) return selector // (no arguments)

            val arguments = s.substring(s.indexOf('[') + 1, s.indexOf(']'))
            val pairs = arguments.split(",")
            for(p in pairs) {
                if(p.isEmpty()) continue

                val index = p.indexOf('=')
                if(index == -1 || index == p.lastIndex) {
                    selector.add(SelectorArgument(p, emptyArray()))
                    continue
                }

                val argName = p.substring(0, index)
                val input = p.substring(index + 1, p.length).split("\\|")
                selector.add(SelectorArgument(argName, input.toTypedArray()))
            }
            return selector
        }
    }

}