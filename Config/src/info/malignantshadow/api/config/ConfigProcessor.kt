package info.malignantshadow.api.config

import java.io.InputStream

/**
 * Represents an object that processes a config file or source string
 * @author Shad0w (Caleb Downs
 */
interface ConfigProcessor {

    /**
     * Get a [ConfigSection] from the given stream
     * @param stream The stream
     */
    operator fun get(stream: InputStream): ConfigSection?

}