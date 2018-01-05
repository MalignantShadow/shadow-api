package info.malignantshadow.api.util.arguments

interface ArgumentHolder {

    fun withArg(arg: Argument) : ArgumentHolder
    fun withArgs(args: Iterable<Argument>) : ArgumentHolder

    operator fun Argument.unaryPlus() {
        withArg(this)
    }

    operator fun Iterable<Argument>.unaryPlus() {
        withArgs(this)
    }

}