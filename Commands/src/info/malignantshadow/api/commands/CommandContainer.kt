package info.malignantshadow.api.commands

import info.malignantshadow.api.commands.dispatch.Source

open class CommandContainer(val children: List<Command>) {

    fun getVisibleChildren(source: Source) = children.filter { it.isSendableBy(source) }

    @JvmName("getCommand")
    operator fun get(name: String) = children.firstOrNull { it.hasAlias(name, true) }

}