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

    /**
     * Write a [ConfigSection] to file represented by the given path
     *
     * @param path The path to the file
     * @param section The ConfigSection
     */
    operator fun set(path: Path, section: ConfigSection): Boolean {
        if (Files.exists(path) && Files.isDirectory(path)) return false
        return set(FileWriter(path.toString()), section)
    }

    /**
     * Write a [ConfigSection] to the given [FileWriter]
     *
     * @param writer The file writer
     * @param section The ConfigSection
     */
    abstract operator fun set(writer: FileWriter, section: ConfigSection): Boolean

}