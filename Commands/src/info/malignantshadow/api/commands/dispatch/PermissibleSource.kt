package info.malignantshadow.api.commands.dispatch

open class PermissibleSource : Source() {

    private val _permissions = ArrayList<String>()
    val permissions: List<String> get() = _permissions.toList()

    override fun hasPermission(permission: String) = _permissions.any { it == permission }

    open fun grant(permission: String) {
        _permissions.add(permission)
    }

    open fun revoke(permsission: String) {
        _permissions.remove(permsission)
    }

}