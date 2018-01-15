package info.malignantshadow.api.commands

class CommandContext<C: Command<C, S>, S: CommandSender>(
        val prefix: String,
        val cmd: C,
        val sender: S,
        val parts: List<Command.Part>
) {

    val extra = parts.filter { it.isExtra }

    operator fun get(name: String) = getPart(name)?.value

    fun getPart(name: String) = parts.firstOrNull { it.arg?.name == name }

    fun isPresent(name: String) = getPart(name) != null

    fun dispatchSelf() = cmd.handler?.invoke(this)

}