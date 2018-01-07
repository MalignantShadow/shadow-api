package info.malignantshadow.api.config.processor

import info.malignantshadow.api.config.ConfigProcessor
import info.malignantshadow.api.config.ConfigSection
import java.nio.file.Files
import java.nio.file.Path

abstract class FileConfigProcessor : ConfigProcessor {

    /**
     * Get a [ConfigSection] from the given [Path] representing a file
     *
     * @param path The path to the file
     * @return a ConfigSection, or null if the file does not exist, is a directory, or an exception occurs
     */
    operator fun get(path: Path): ConfigSection? {
        if (Files.isDirectory(path) || !Files.exists(path)) return null

        return try {
            get(Files.newInputStream(path))
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

}