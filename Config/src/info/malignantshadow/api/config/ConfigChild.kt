package info.malignantshadow.api.config

abstract class ConfigChild {

    open internal var parentInternal: ConfigChild? = null
    val parent: ConfigChild? get() = parentInternal

    abstract fun isLastInTree(): Boolean
}
