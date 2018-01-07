package info.malignantshadow.api.config

open class ConfigSequence : ConfigChild(), Iterable<Any?>, ConfigCopyable {

    private val _seq = ArrayList<Any?>()

    /**
     * A copy of the list items
     */
    val list get() = ArrayList(_seq)

    /**
     * The size of this list
     */
    val size get() = _seq.size

    /**
     * Indicates whether this list is empty
     *
     * @return `true` if this list is empty
     */
    fun isEmpty() = _seq.isEmpty()

    /**
     * The last index of this list
     */
    val lastIndex get() = _seq.lastIndex

    companion object Static {

        /**
         * Construct a new ConfigSequence from the given Iterable
         *
         * @param arr An [Iterable] object
         * @return a new ConfigSequence
         */
        @JvmStatic
        fun from(arr: Iterable<Any?>): ConfigSequence {
            val seq = ConfigSequence()
            arr.forEach { seq.add(it) }
            return seq
        }

    }

    /**
     * Add a value to this ConfigSequence
     *
     * @param value The value to add
     */
    operator fun plusAssign(value: Any?) = add(value)

    /**
     * Add a value to this ConfigSequence
     *
     * @param value The value to add
     * @param index The index to put it at
     */
    @JvmOverloads
    open fun add(value: Any?, index: Int = _seq.size) {
        val v = Configs.getValue(value)
        if (v is ConfigChild) v.parentInternal = this
        _seq.add(v)
    }

    /**
     * Add all of the given values to this ConfigSequence
     *
     * @param values: The values to add
     */
    @JvmOverloads
    open fun addAll(values: Iterable<Any?>, index: Int = _seq.size) {
        var i = index
        values.forEach { add(it, i); i++ }
    }

    /**
     * Get the value at the specified index
     *
     * @param index The index of the values
     * @return the value
     */
    operator fun get(index: Int) = _seq[index]
    operator fun set(index: Int, value: Any?) {
        _seq[index] = value
    }

    /**
     * Get the value at specified index, or null if the index doesn't exist
     *
     * @param index The index
     * @return the value, or null if the value doesn't exist
     */
    fun getOrNull(index: Int) = _seq.getOrNull(index)

    /**
     * Get the value at the given index, or `def` if the value is `null`, not of type `T`, or
     * doesn't exist
     *
     * @param T The type
     * @param def The default value
     * @param index The index of the value
     * @return the value or `def` if the value is null or doesn't exist
     */
    inline fun <reified T> get(def: T, index: Int): T = getOrNull(index) as? T ?: def

    /**
     * Get the value at the given index, as a Number or `def` if the value is `null`, not a Number, or
     * doesn't exist
     *
     * @param def The default value
     * @param index The index of the value
     * @return the value or `def` if the value is null or doesn't exist
     */
    @JvmOverloads
    fun getNumber(def: Number = 0, index: Int): Number {
        val v = get(index)
        return v as? Number ?: (v as? ConfigNumber<*>)?.value ?: def
    }

    /**
     * Get the value at the given index as a String, or `def` if the value is null or doesn't exist
     *
     * @param def The default value
     * @param index The index of the value
     * @return the value or `def` if the value is null or doesn't exist
     */
    @JvmOverloads
    fun getString(def: String = "", index: Int) = get(def, index).toString()

    /**
     * Get the value at the given index, as a Boolean or `def` if the value is `null`, not a Boolean, or
     * doesn't exist
     *
     * @param def The default value
     * @param index The index of the value
     * @return the value or `def` if the value is null or doesn't exist
     */
    @JvmOverloads
    fun getBoolean(def: Boolean = false, index: Int): Boolean {
        val v = get(index)
        return v as? Boolean ?: (v as? ConfigBoolean)?.value ?: def
    }

    /**
     * Get the value at the given index, as a ConfigSequence or `def` if the value is `null`,
     * not a ConfigSequence, or doesn't exist
     *
     * @param def The default value
     * @param index The index of the value
     * @return the value or `def` if the value is null or doesn't exist
     */
    @JvmOverloads
    fun getSequence(def: ConfigSequence = ConfigSequence(), index: Int) = get(def, index)

    /**
     * Get the value at the given index, as a [ConfigSection] or `def` if the value is `null`,
     * not a [ConfigSection], or doesn't exist
     *
     * @param def The default value
     * @param index The index of the value
     * @return the value or `def` if the value is null or doesn't exist
     */
    @JvmOverloads
    fun getSection(def: ConfigSection = ConfigSection(), index: Int) = get(def, index)

    /**
     * Convert this ConfigSequence to a list
     * @return a list that accurately represents this ConfigSequence
     */
    fun toList(): ArrayList<Any?> {
        val list = ArrayList<Any?>()
        _seq.forEach { list.add(when(it) {
            is ConfigSection -> it.toMap()
            is ConfigSequence -> it.toList()
            is ConfigPair -> ConfigSection()[it.key] = it.value
            is ConfigValue<*> -> it.value
            else -> it
        }) }
        return list
    }

    override fun iterator(): Iterator<Any?> = _seq.iterator()

    override fun equals(other: Any?): Boolean = if (other is ConfigSequence) equals(other) else super.equals(other)

    /**
     * Indicates whether the given ConfigSequence is equal to this one. All values in `other` must equal to their
     * corresponding index in this ConfigSequence
     *
     * @param other The other ConfigSequence
     * @return `true` if `other` is structurally identical to this ConfigSequence
     */
    fun equals(other: ConfigSequence): Boolean {
        if (other === this) return true
        if (other.size != size) return false

        _seq.forEachIndexed { index, it -> if (it != other[index]) return@equals false }
        return true
    }

    override fun copy(): ConfigSequence {
        val seq = ConfigSequence()
        forEach { seq.add((it as? ConfigCopyable)?.copy() ?: it) }
        return seq
    }

    override fun hashCode(): Int {
        return _seq.hashCode()
    }

}