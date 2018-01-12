package info.malignantshadow.api.config

import info.malignantshadow.api.util.build

/**
 * Represents a object that holds a list of key/value pairs, where no key can occur twice.
 * @author Shad0w (Caleb Downs)
 */
@ConfigDsl
class ConfigSection : ConfigChild(), Iterable<ConfigPair>, ConfigCopyable {

    @Suppress("ArrayInDataClass")
    private data class Path(val key: String, val path: Array<out String>)

    companion object Static {

        private fun newPath(vararg path: String) = Path(path[0], path.sliceArray(1..path.lastIndex))

        /**
         * Construct a ConfigSection from the given [Map]
         * @param map The map
         */
        @JvmStatic
        fun from(map: Map<*, *>): ConfigSection = from(map.toList())

        /**
         * Construct a ConfigSection from a list of [Pair]s
         */
        @JvmStatic
        fun from(entries: Iterable<Pair<*, *>>): ConfigSection {
            val root = ConfigSection()
            entries.forEach loop@ { (k, v) ->
                if (k == null) return@loop
                val key = k as? String ?: k.toString()
                root[key] = Configs.getValue(v)
            }
            return root
        }

    }

    private val _pairs = arrayListOf<ConfigPair>()

    /**
     * A copy of the list of pairs
     */
    val pairs get() = _pairs.toList()

    /**
     * The size of this section, equivalent to `pairs.size`
     */
    val size get() = _pairs.size

    /**
     * Indicates whether this ConfigSection is empty (has no pairs). Equivalent to
     * `pairs.isEmpty()`
     * @return true if this ConfigSection is empty
     */
    fun isEmpty() = _pairs.isEmpty()

    /**
     * The last index of this ConfigSection . Equivalent to `pairs.lastIndex`
     */
    val lastIndex get() = _pairs.lastIndex

    /**
     * Set a value in this ConfigSection
     *
     * @param key The key
     * @param path Further defined keys to acts as a path
     * @return this
     */
    operator fun set(key: String, vararg path: String, value: Any?): ConfigSection {
        // get() with create=true will always return non-null
        get(true, key, *path)?.value = value
        return this
    }

    /**
     * Set a value only if the pair at the given path is missing
     *
     * @param key The key
     * @param path Further defined keys to acts as a path
     * @param value The value
     * @return this
     */
    fun setIfAbsent(key: String, vararg path: String, value: Any?): ConfigSection {
        var pair = get(key, *path)
        if (pair == null) {
            pair = createPairing(key, *path)
            pair.value = value
        }
        return this
    }

    /**
     *
     *
     * @param T The type
     * @param isFn A function to determine whether the value is of type `T`
     * @param asFn A function to transform the value into the type `T`
     * @param def The default value
     * @param key The key
     * @param path Further defined keys to acts as a path
     * @return the value as type T, or the default if the pair is null or `isFn` returns false
     */
    fun <T> get(isFn: ConfigPair.() -> Boolean, asFn: ConfigPair.() -> T, def: T, key: String, vararg path: String): T {
        val pair = get(key, *path)
        return if (pair?.isFn() == true) pair.asFn() else def
    }

    /**
     * Get the value of the pair at the given path as a [Number]
     *
     * @param def The default value
     * @param key The key
     * @param path Further defined keys to acts as a path
     */
    @JvmOverloads
    fun getNumber(def: Number = 0, key: String, vararg path: String)
            = get(ConfigPair::isNumber, ConfigPair::asNumber, def, key, *path)

    /**
     * Get the value of the pair at the given path as a String
     *
     * @param key The key
     * @param path Further defined keys to acts as a path
     */
    fun getString(key: String, vararg path: String = arrayOf()) = getStringWithDefault("", key, *path)

    /**
     * Get the value of the pair at the given path as a String
     *
     * @param def The default value
     * @param key The key
     * @param path Further defined keys to acts as a path
     */
    fun getStringWithDefault(def: String, key: String, vararg path: String) =
            get(ConfigPair::isString, ConfigPair::asString, def, key, *path)

    /**
     * Get the value of the pair at the given path as a Boolean
     *
     * @param def The default value
     * @param key The key
     * @param path Further defined keys to acts as a path
     */
    @JvmOverloads
    fun getBoolean(def: Boolean = false, key: String, vararg path: String) =
            get(ConfigPair::isBoolean, ConfigPair::asBoolean, def, key, *path)

    /**
     * Get the value of the pair at the given path as a ConfigSection
     *
     * @param def The default value
     * @param key The key
     * @param path Further defined keys to acts as a path
     */
    @JvmOverloads
    fun getSection(def: ConfigSection = ConfigSection(), key: String, vararg path: String) =
            get(ConfigPair::isSection, ConfigPair::asSection, def, key, *path)

    /**
     * Get the value of the pair at the given path as a [ConfigSequence]
     *
     * @param def The default value
     * @param key The key
     * @param path Further defined keys to acts as a path
     */
    @JvmOverloads
    fun getSequence(def: ConfigSequence = ConfigSequence(), key: String, vararg path: String) =
            get(ConfigPair::isSequence, ConfigPair::asSequence, def, key, *path)

    /**
     * Get a [ConfigPair] from this ConfigSection
     *
     * @param key The key
     * @param path Further defined keys to acts as a path
     * @return the [ConfigPair] found, or null if it wasn't found
     */
    operator fun get(key: String, vararg path: String): ConfigPair? = get(false, key, *path)

    /**
     * Get a [ConfigPair] from this ConfigSection. This function will never return `null` if `create`
     * is `true`
     *
     * @param create Whether a [ConfigPair] should be created if not found
     * @param key The key
     * @param path Further defined keys to acts as a path
     * @return the [ConfigPair] found, or null if it wasn't found
     */
    fun get(create: Boolean, key: String, vararg path: String): ConfigPair? {
        for (it in _pairs) {
            if (it.key != key) continue
            if (path.isEmpty()) return it

            val newPath = newPath(*path)

            if (!it.isSection() && create) it.value = ConfigSection()

            return it.asSection().get(create, newPath.key, *newPath.path)
        }

        if (create) return createPairing(key, *path)

        return null
    }

    private fun createPairing(key: String, vararg path: String): ConfigPair {
        val pair = key withValue null
        _pairs.add(pair)
        if (path.isEmpty()) return pair

        val newPath = newPath(*path)
        pair.value = ConfigSection()
        pair.parentInternal = this
        return pair.asSection().createPairing(newPath.key, *newPath.path)
    }

    /**
     * Add a [ConfigPair] to this ConfigSection
     */
    operator fun plusAssign(pair: ConfigPair) = add(pair)

    /**
     * Add a [ConfigPair] to this ConfigSection
     */
    fun add(pair: ConfigPair) {
        pair.parentInternal = this
        val value = pair.value
        if (value is ConfigChild) value.parentInternal = pair
        val index = _pairs.indexOfFirst { it.key == pair.key }
        if (index == -1) _pairs.add(pair)
        else _pairs[index] = pair
    }

    /**
     * Add all of the [ConfigPair]s to this ConfigSection
     */
    operator fun plusAssign(pairs: Iterable<ConfigPair>) = addAll(pairs)

    /**
     * Add all of the [ConfigPair]s to this ConfigSection
     */
    fun addAll(pairs: Iterable<ConfigPair>) {
        pairs.forEach { add(it) }
    }

    /**
     * Remove all pairs that match the given predicate
     *
     * @param filter The predicate to use
     * @return `true` if any elements were removed
     */
    fun removeIf(filter: (ConfigPair) -> Boolean) = _pairs.removeIf(filter)

    /**
     * Remove the pair with the given key
     *
     * @param key The key of the pair to remove
     * @return `true` if it was removed
     */
    fun remove(key: String) = removeIf { it.key == key }

    /**
     * Remove all pairs from the ConfigSection
     */
    fun clear() = _pairs.clear()

    /**
     * Convert this ConfigSection to a [HashMap]
     *
     * @return a map that accurately represents this ConfigSection
     */
    fun toMap(): HashMap<String, Any?> {
        val map = LinkedHashMap<String, Any?>()
        _pairs.forEach { (k, v) ->
            map.put(k, when (v) {
                is ConfigSection -> v.toMap()
                is ConfigSequence -> v.toList()
                is ConfigValue<*> -> v.value
                else -> v
            })
        }
        return map
    }

    override fun iterator(): Iterator<ConfigPair> = _pairs.iterator()

    override fun equals(other: Any?) = if (other is ConfigSection) equals(other) else super.equals(other)

    /**
     * Indicates whether the given ConfigSection is equal to this one. That is, if they are *structurally* identical.
     * As long as both sections have every key and their values are identical, this function returns true. The order of
     * the keys is not important, only their presence and their value is required.
     *
     * @param other The other ConfigSection
     * @return `true` if the two sections are structurally identical
     */
    fun equals(other: ConfigSection): Boolean {
        if (other === this) return true
        if (other.size != size) return false

        pairs.forEach { if (it != other[it.key]) return@equals false }
        return true
    }

    fun section(key: String, init: ConfigSection.() -> Unit) {
        get(true, key)!!.value = build(ConfigSection(), init)
    }

    fun sequence(key: String, init: ConfigSequence.() -> Unit) {
        get(true, key)!!.value = build(ConfigSequence(), init)
    }

    fun value(key: String, lazyValue: () -> Any?) {
        get(true, key)!!.value = lazyValue()
    }

    override fun copy(): ConfigSection {
        val copy = ConfigSection()
        forEach { copy.add(it) }
        return copy
    }

    override fun hashCode(): Int {
        return _pairs.hashCode()
    }

}