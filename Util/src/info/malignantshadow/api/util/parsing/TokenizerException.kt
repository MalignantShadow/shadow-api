package info.malignantshadow.api.util.parsing

class TokenizerException(message: String, val near: String) : RuntimeException(message) {

    private companion object {
        const val LIMIT = 30
    }

    override val message
        get() =
            super.message +
                    (if (!near.isEmpty()) " near " else "") +
                    if (near.length > LIMIT) near.substring(0 until LIMIT) + "..." else near

}