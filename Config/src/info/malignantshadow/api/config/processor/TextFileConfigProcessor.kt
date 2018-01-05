package info.malignantshadow.api.config.processor

import info.malignantshadow.api.config.ConfigSection
import java.io.FileWriter
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.Scanner

abstract class TextFileConfigProcessor : FileConfigProcessor() {

    abstract operator fun get(src: String): ConfigSection?

    override operator fun get(stream: InputStream): ConfigSection? {
        val scanner = Scanner(stream)
        scanner.useDelimiter("\\A")
        val source = scanner.next()
        scanner.close()
        return get(source)
    }

    operator fun set(path: Path, document: ConfigSection): Boolean {
        if (Files.exists(path) && Files.isDirectory(path)) return false
        return set(FileWriter(path.toString()), document)
    }

    abstract operator fun set(writer: FileWriter, document: ConfigSection): Boolean

}