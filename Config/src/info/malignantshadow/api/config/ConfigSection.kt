package info.malignantshadow.api.config

class ConfigSection : ConfigChild(), Iterable<ConfigPair>, ConfigCopyable {

    @Suppress("ArrayInDataClass")
    private data class Path(val key: String, val path: Array<out String>)

    companion object Static {

        private fun newPath(vararg path: String) = Path(path[0], path.sliceArray(1..path.lastIndex))

        @JvmStatic
        fun from(map: Map<*, *>): ConfigSection = from(map.toList())

        @JvmStatic
        fun from(map: HashMap<*, *>): ConfigSection = from(map.toList())

        @JvmStatic
        fun from(entries: Iterable<Pair<*, *>>): ConfigSection {
            val root = ConfigSection()
            entries.forEach loop@{ (k, v) ->
                if(k == null) return@loop
                val key = k as? String ?: k.toString()
                root[key] = Configs.getValue(v)
            }
            return root
        }

    }

    private val _pairs = arrayListOf<ConfigPair>()
    val pairs get() = _pairs.toList()

    val size get() = _pairs.size
    fun isEmpty() = _pairs.isEmpty()
    val lastIndex get() = _pairs.lastIndex

    // isSet setIf setIfMissing get<Type>

    operator fun set(key: String, vararg path: String, value: Any?): ConfigSection {
        // get() with create=true will always return non-null
        get(true, key, *path)?.value = value
        return this
    }

    fun setIfAbsent(key: String, vararg path: String, value: Any?): ConfigSection {
        var pair = get(key, *path)
        if(pair == null) {
            pair = createPairing(key, *path)
            pair.value = value
        }
        return this
    }

    fun <T> get(isFn: ConfigPair.() -> Boolean, asFn: ConfigPair.() -> T, def: T, key: String, vararg path: String): T {
        val pair = get(key, *path)
        return if(pair?.isFn() == true) pair.asFn() else def
    }

    @JvmOverloads
    fun getNumber(def: Number = 0, key: String, vararg path: String)
            = get(ConfigPair::isNumber, ConfigPair::asNumber, def, key, *path)

    fun getString(key: String, vararg path: String = arrayOf()) = getStringWithDefault("", key, *path)
    fun getStringWithDefault(def: String, key: String, vararg path: String)=
            get(ConfigPair::isString, ConfigPair::asString, def, key, *path)

    @JvmOverloads
    fun getBoolean(def: Boolean = false, key: String, vararg path: String) =
            get(ConfigPair::isBoolean, ConfigPair::asBoolean, def, key, *path)

    @JvmOverloads
    fun getSection(def:  ConfigSection = ConfigSection(), key: String, vararg path: String) =
            get(ConfigPair::isSection, ConfigPair::asSection, def, key, *path)

    @JvmOverloads
    fun getSequence(def: ConfigSequence = ConfigSequence(), key: String, vararg path: String) =
            get(ConfigPair::isSequence, ConfigPair::asSequence, def, key, *path)

    operator fun get(key: String, vararg path: String): ConfigPair? = get(false, key, *path)
    fun get(create: Boolean, key: String, vararg path: String): ConfigPair? {
        for(it in _pairs) {
            if(it.key != key) continue
            if(path.isEmpty()) return it

            val newPath = newPath(*path)

            if(!it.isSection() && create) it.value = ConfigSection()

            return it.asSection().get(create, newPath.key, *newPath.path)
        }

        if(create) return createPairing(key, *path)

        return null
    }

    private fun createPairing(key: String, vararg path: String): ConfigPair {
        val pair = key withValue null
        _pairs.add(pair)
        if(path.isEmpty()) return pair

        val newPath = newPath(*path)
        pair.value = ConfigSection()
        pair.parentInternal = this
        return pair.asSection().createPairing(newPath.key, *newPath.path)
    }

    operator fun plusAssign(pair: ConfigPair) = add(pair)
    fun add(pair: ConfigPair) {
        pair.parentInternal = this
        val value = pair.value
        if(value is ConfigChild) value.parentInternal = pair
        val index = _pairs.indexOfFirst { it.key == pair.key }
        if(index == -1) _pairs.add(pair)
        else _pairs[index] = pair
    }

    operator fun plusAssign(pairs: Iterable<ConfigPair>) = addAll(pairs)
    fun addAll(pairs: Iterable<ConfigPair>) {
        pairs.forEach { add(it) }
    }

    fun removeIf(filter: (ConfigPair) -> Boolean) = _pairs.removeIf(filter)
    fun remove(key: String) = removeIf { it.key == key }
    fun clear() = _pairs.clear()

    fun toMap(): HashMap<String, Any?> {
        val map = HashMap<String, Any?>()
        _pairs.forEach { (k, v) ->
            map.put(k, when(v) {
                is ConfigSection -> v.toMap()
                is ConfigSequence -> v.toList()
                else -> v
            })
        }
        return map
    }

    fun <T> transformTo(transformer: (ConfigSection) -> T?): T? = transformer(this)

    override fun iterator(): Iterator<ConfigPair> = _pairs.iterator()

    override fun equals(other: Any?) =  if(other is ConfigSection) equals(other) else super.equals(other)

    fun equals(other: ConfigSection): Boolean {
        if(other === this) return true
        if(other.size != size) return false

        pairs.forEach {  if(it != other[it.key]) return@equals false }
        return true
    }

    override fun copy(): ConfigSection {
        val copy = ConfigSection()
        forEach { copy.add(it) }
        return copy
    }

    override fun hashCode(): Int {
        return _pairs.hashCode()
    }

    override fun isLastInTree(): Boolean {
        if(parent == null) return true // root
        if(parent is ConfigPair) return (parent as ConfigPair).isLastInTree()
        if(parent is ConfigSequence) {
            val p = (parent as ConfigSequence)
            val lastLocal = p[p.lastIndex] == this
            return lastLocal && p.isLastInTree()
        }
        throw IllegalStateException("parent is not null, ConfigPair or ConfigSequence")
    }

}