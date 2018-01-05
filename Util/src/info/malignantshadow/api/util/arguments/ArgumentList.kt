package info.malignantshadow.api.util.arguments

class ArgumentList(val argsList: ArrayList<Argument>) : Iterable<Argument>, ArgumentHolder {

    override fun withArg(arg: Argument): ArgumentList {
        argsList.add(arg)
        return this
    }

    override fun withArgs(args: Iterable<Argument>): ArgumentList {
        argsList.addAll(args)
        return this
    }

    private var _extra: Argument? = null
    val extra: Argument? get() = _extra

    val min: Int
        get() {
            var count: Int = argsList.count { it.required }
            _extra?.required.let { count++ }
            return count
        }

    constructor() : this(ArrayList<Argument>())
    constructor(args: ArgumentList) : this(args.argsList)
    constructor(init: ArgumentList.() -> Unit) : this() {
        this.init()
    }

    fun setExtraArgument(desc: String?, required: Boolean = false, display: String = ""): ArgumentList {
        _extra = Argument("extra", desc, required, display)
        return this
    }


    fun isEmpty() = argsList.isEmpty()

    operator fun get(index: Int): Argument = argsList[index]
    operator fun get(name: String): Argument? = argsList.firstOrNull { it.name == name }

    fun getRequired() = argsList.filter { it.required }
    fun getOptional() = argsList.filter { !it.required }

    override fun iterator() = argsList.iterator()

}