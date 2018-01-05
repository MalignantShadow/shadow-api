package info.malignantshadow.api.config.processor

import info.malignantshadow.api.config.ConfigSection
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

abstract class BinaryFileConfigProcessor : FileConfigProcessor() {

    operator fun set(path: String, document: ConfigSection)  =
            set(FileOutputStream(path), document)

    operator fun set(path: Path, document: ConfigSection): Boolean {
        if(Files.exists(path) && Files.isDirectory(path)) return false

        return set(FileOutputStream(path.toString()), document)
    }

    abstract operator fun set(stream: FileOutputStream, document: ConfigSection): Boolean

}