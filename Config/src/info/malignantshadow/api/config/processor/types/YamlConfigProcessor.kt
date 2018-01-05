package info.malignantshadow.api.config.processor.types

import info.malignantshadow.api.config.ConfigSection
import info.malignantshadow.api.config.processor.TextFileConfigProcessor
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.FileWriter
import java.io.IOException

class YamlConfigProcessor : TextFileConfigProcessor {

    private lateinit var _yaml: Yaml
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

    @JvmOverloads
    constructor(indentSize: Int = 2, width: Int = 200, blockStyle: Boolean = true) :
            this(createDumperOptions(indentSize, width, if (blockStyle) DumperOptions.FlowStyle.BLOCK else DumperOptions.FlowStyle.FLOW))

    constructor(options: DumperOptions) {
        _yaml = Yaml(options)
    }

    override fun get(src: String) = get(_yaml.load(src))

    private fun get(it: Any?): ConfigSection? {
        if(it !is Map<*, *>) return null
        return ConfigSection.from(it)
    }

    override fun set(writer: FileWriter, document: ConfigSection): Boolean {
        return try {
            _yaml.dump(document.toMap(), writer)
            writer.close()
            true
        } catch(e: IOException) {
            false
        }
    }

    fun dump(document: ConfigSection) = _yaml.dump(document.toMap())

}