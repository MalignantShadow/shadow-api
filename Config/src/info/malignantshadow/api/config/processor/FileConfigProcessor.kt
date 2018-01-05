package info.malignantshadow.api.config.processor

import info.malignantshadow.api.config.ConfigProcessor
import info.malignantshadow.api.config.ConfigSection
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

abstract class FileConfigProcessor : ConfigProcessor {

    operator fun get(path: Path): ConfigSection? {
        if(Files.isDirectory(path) || !Files.exists(path)) return null

        return try {
            get(Files.newInputStream(path))
        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }

}