package info.malignantshadow.api.config.processor.types

import info.malignantshadow.api.config.ConfigSection
import info.malignantshadow.api.config.processor.TextFileConfigProcessor
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.FileWriter
import java.io.IOException

/**
 * A [info.malignantshadow.api.config.ConfigProcessor] that is used to handle JSON configurations. Internally,
 * this class utilizes an instance of [Yaml] as a backend.
 * @author Shad0w (Caleb Downs)
 */
class YamlConfigProcessor : TextFileConfigProcessor {

    private lateinit var _yaml: Yaml

    /**
     * The [Yaml] object used for the backend
     */
    val yaml = _yaml

    private companion object {

        fun createDumperOptions(indentSize: Int, width: Int, style: DumperOptions.FlowStyle): DumperOptions {
            val options = DumperOptions()
            options.defaultFlowStyle = style
            options.width = width
            options.indent = indentSize
            options.lineBreak = DumperOptions.LineBreak.UNIX
            return options
        }

    }

    /**
     * Construct a YamlConfigProcessor with the given options to be used in a [DumperOptions] object
     */
    @JvmOverloads
    constructor(indentSize: Int = 2, width: Int = 200, blockStyle: Boolean = true) :
            this(createDumperOptions(indentSize, width, if (blockStyle) DumperOptions.FlowStyle.BLOCK else DumperOptions.FlowStyle.FLOW))

    /**
     * Construct a YamlConfigProcessor with the given [DumperOptions]
     */
    constructor(options: DumperOptions) {
        _yaml = Yaml(options)
    }

    /**
     * Get a ConfigSection from the given YAML source
     */
    override fun get(src: String) = get(_yaml.load(src))

    private fun get(it: Any?): ConfigSection? {
        if (it !is Map<*, *>) return null
        return ConfigSection.from(it)
    }

    override fun set(writer: FileWriter, section: ConfigSection): Boolean {
        return try {
            _yaml.dump(section.toMap(), writer)
            writer.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    /**
     * Stringify a [ConfigSection] as a YAML string
     *
     * @param section The ConfigSection
     */
    fun dump(section: ConfigSection) = _yaml.dump(section.toMap())

}