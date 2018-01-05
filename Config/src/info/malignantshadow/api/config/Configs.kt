package info.malignantshadow.api.config

import info.malignantshadow.api.config.processor.types.JsonConfigProcessor
import info.malignantshadow.api.config.processor.types.ShadeConfigProcessor
import info.malignantshadow.api.config.processor.types.YamlConfigProcessor

object Configs {

    object FileAssociations {

        @JvmStatic
        operator fun get(ext: String): ConfigProcessor? = when (ext) {
            "json" -> Configs.json()
            "yaml", "yml" -> Configs.yaml()
            "shade", "shd" -> Configs.shade()
            else -> null
        }
    }


    @JvmStatic
    fun testPath(vararg path: String): Boolean = !path.isEmpty()

    @JvmStatic
    fun checkPath(vararg path: String) =
            if (!testPath(*path)) throw IllegalArgumentException("'path' must have a length >= 1")
            else Unit


    @JvmStatic
    fun getValue(value: Any?): Any? = when(value) {
        is List<*> -> ConfigSequence.from(value)
        is Map<*, *> -> ConfigSection.from(value)
        else -> value
    }

    @JvmStatic
    @JvmOverloads
    fun yaml(indentSize: Int = 2, width: Int = 200, blockStyle: Boolean = true) =
            YamlConfigProcessor(indentSize, width, blockStyle)

    @JvmStatic
    fun json() = JsonConfigProcessor()

    @JvmStatic
    fun shade() = ShadeConfigProcessor()

}