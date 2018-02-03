package info.malignantshadow.api.commands.dispatch

/**
 * Represents a command source whose permissions can be granted and revoked at any time.
 *
 * @author Shad0w (Caleb Downs)
 */
open class PermissibleSource : Source() {

    private val _permissions = ArrayList<String>()

    /**
     * The permissions this command source
     */
    val permissions: List<String> get() = _permissions.toList()

    override fun hasPermission(permission: String) = _permissions.any { it == permission }

    /**
     * Grants a permission to this command source.
     *
     * @param permission The permission
     */
    open fun grant(permission: String) {
        _permissions.add(permission)
    }

    /**
     * Revokes a permission to this command source.
     *
     * @param permission The permission
     */
    open fun revoke(permission: String) {
        _permissions.remove(permission)
    }

}