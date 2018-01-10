package info.malignantshadow.api.util.arguments

/**
 * Represents an object that contains arguments
 *
 * @author Shad0w (Caleb Downs)
 */
interface ArgumentHolder {

    /**
     * Add an argument to this argument holder and return it
     *
     * @param arg The argument
     * @return this
     */
    fun withArg(arg: Argument) : ArgumentHolder

    /**
     * Add the argument to this argument holder and return it
     *
     * @param args The arguments
     * @return this
     */
    fun withArgs(args: Iterable<Argument>) : ArgumentHolder

    /**
     * Add an [Argument]
     */
    operator fun Argument.unaryPlus() {
        withArg(this)
    }

    /**
     * Add multiple [Argument]s
     */
    operator fun Iterable<Argument>.unaryPlus() {
        withArgs(this)
    }

}