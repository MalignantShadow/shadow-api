package info.malignantshadow.api.config.processor

import info.malignantshadow.api.config.ConfigSection
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

abstract class BinaryFileConfigProcessor : FileConfigProcessor() {

    /**
     * Write a [ConfigSection] to file represented by the given path
     *
     * @param path The path to the file
     * @param section The ConfigSection
     */
    operator fun set(path: String, section: ConfigSection) =
            set(FileOutputStream(path), section)

    /**
     * Write a [ConfigSection] to file represented by the given path
     *
     * @param path The path to the file
     * @param section The ConfigSection
     */
            operator fun set(path: Path, section: ConfigSection): Boolean {
        if (Files.exists(path) && Files.isDirectory(path)) return false

        return set(FileOutputStream(path.toString()), section)
    }

    /**
     * Write a [ConfigSection] to the given [FileOutputStream]
     *
     * @param stream The output stream
     * @param section The ConfigSection
     */
    abstract operator fun set(stream: FileOutputStream, section: ConfigSection): Boolean

}