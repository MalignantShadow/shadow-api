package info.malignantshadow.api.config

import java.io.InputStream

interface ConfigProcessor {

    operator fun get(stream: InputStream): ConfigSection?

}