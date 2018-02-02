package info.malignantshadow.api.commands.build.attributes

interface Describable {

    /**
     * Sets the description of this object
     * @param desc The description
     */
    fun description(desc: String)

}

class SimpleDescribable: Describable {

    var description = ""
        private set

    override fun description(desc: String) {
        description  = desc
    }

}