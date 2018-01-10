package info.malignantshadow.api.util.arguments

/**
 * Represents a list of [Argument]s
 *
 * @author Shad0w (Caleb Downs)
 *
 */
class ArgumentList(

        /**
         * The list of arguments
         */
        val argsList: ArrayList<Argument>

) : Iterable<Argument>, ArgumentHolder {

    override fun withArg(arg: Argument): ArgumentList {
        argsList.add(arg)
        return this
    }

    override fun withArgs(args: Iterable<Argument>): ArgumentList {
        argsList.addAll(args)
        return this
    }

    private var _extra: Argument? = null

    /**
     * The extra arguments
     */
    val extra: Argument? get() = _extra

    /**
     * The minimum amount of arguments
     */
    val min: Int
        get() = argsList.count { it.required } +
                if (_extra != null) 1 else 0


    /**
     * Construct an empty ArgumentList
     */
    constructor() : this(ArrayList<Argument>())

    /**
     * Construct an ArgumentList using a copy of the given ArgumentList
     *
     * @param args The ArgumentList to copy
     */
    constructor(args: ArgumentList) : this(ArrayList(args.argsList))

    /**
     * Construct an empty ArgumentList then invoke the given extension function
     *
     * @param init The extension function
     */
    constructor(init: ArgumentList.() -> Unit) : this() {
        this.init()
    }

    /**
     * Set the parameters of the extra [Argument] in this ArgumentList
     *
     * @param desc The description of the argument
     * @param required Whether the argument is required
     * @param display The name of the argument to display instead of "extra"
     */
    fun setExtraArgument(desc: String?, required: Boolean = false, display: String = ""): ArgumentList {
        _extra = Argument("extra", desc, required, display)
        return this
    }


    /**
     * Indicates whether this ArgumentList is empty
     *
     * @return `true` if this ArgumentList is empty
     */
    fun isEmpty() = argsList.isEmpty()

    /**
     * Get an [Argument] from this ArgumentList
     *
     * @param index The index of the argument
     * @return the argument, or null if it doesn't exist
     */
    operator fun get(index: Int): Argument? = argsList.getOrNull(index)

    /**
     * Get an [Argument] from this ArgumentList
     *
     * @param name The name of the argument
     * @return the argument, or null if it doesn't exist
     */
    operator fun get(name: String): Argument? = argsList.firstOrNull { it.name == name }

    /**
     * Indicates the amount of [Argument]s in this ArgumentList that are required
     *
     * @return the amount of required arguments
     */
    fun getRequired() = argsList.filter { it.required }


    /**
     * Indicates the amount of [Argument]s in this ArgumentList that are optional
     *
     * @return the amount of optional arguments
     */
    fun getOptional() = argsList.filter { !it.required }

    override fun iterator() = argsList.iterator()

}