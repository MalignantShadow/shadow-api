package info.malignantshadow.api.config

import info.malignantshadow.api.config.processor.types.JsonConfigProcessor
import info.malignantshadow.api.config.processor.types.ShadeConfigProcessor
import info.malignantshadow.api.config.processor.types.YamlConfigProcessor
import info.malignantshadow.api.util.build
import org.yaml.snakeyaml.DumperOptions

@DslMarker
annotation class ConfigDsl

/**
 * Helper object for Configurations
 * @author Shad0w (Caleb Downs)
 */
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
    fun getValue(value: Any?): Any? = when (value) {
        is List<*> -> ConfigSequence.from(value)
        is Map<*, *> -> ConfigSection.from(value)
        else -> value
    }

    @JvmStatic
    @JvmOverloads
    fun yaml(indentSize: Int = 2, width: Int = 200, blockStyle: Boolean = true) =
            YamlConfigProcessor(indentSize, width, blockStyle)

    @JvmStatic
    fun yaml(options: DumperOptions) = YamlConfigProcessor(options)

    @JvmStatic
    fun json() = JsonConfigProcessor()

    @JvmStatic
    fun shade() = ShadeConfigProcessor()

}

fun section(init: ConfigSection.() -> Unit) = build(ConfigSection(), init)

fun sequence(init: ConfigSequence.() -> Unit) = build(ConfigSequence(), init)