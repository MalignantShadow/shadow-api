package info.malignantshadow.api.commands.build.attributes

interface Describable {

    fun description(desc: String)

}

class SimpleDescribable: Describable {

    var description = ""
        private set

    override fun description(desc: String) {
        description  = desc
    }

}