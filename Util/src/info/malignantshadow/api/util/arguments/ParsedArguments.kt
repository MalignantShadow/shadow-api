package info.malignantshadow.api.util.arguments

class ParsedArguments(var args: List<ParsedArgument>) : Iterable<ParsedArgument> {

    private lateinit var _input: List<String>
    private lateinit var _extra: List<String?>

    val input = _input
    val extra = _input

    constructor() : this(ArrayList<ParsedArgument>()) {
        _input = emptyList()
        _extra = emptyList()
    }

    constructor(args: ArgumentList, input: List<String>?) : this() {
        _input = input ?: emptyList()
        val required = args.min
        val given = _input.size
        require (given >= required) { "Not enough arguments given: expected $required, but received $given" }

        val size = args.argsList.size
        _extra = _input.slice((if (size > _input.size) 0 else _input.size - size).._input.lastIndex) //?

        //parsing
        var optionalLeft = _input.size - required
        var inputIndex = 0

        args.forEach {
            when {
                it.required -> this.args += ParsedArgument(it, _input[inputIndex++])
                optionalLeft > 0 -> {
                    optionalLeft--
                    this.args += ParsedArgument(it, _input[inputIndex++])
                }
                else -> this.args += ParsedArgument(it, null)
            }
        }
    }

    operator fun contains(name: String) = get(name) != null
    operator fun get(name: String) = args.firstOrNull { it.arg.name == name }

    fun getValue(name: String): Any? {
        val arg = get(name)
        return arg?.value
    }

    override fun iterator(): Iterator<ParsedArgument> = args.iterator()

}