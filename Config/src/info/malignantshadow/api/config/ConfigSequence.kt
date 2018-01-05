package info.malignantshadow.api.config

open class ConfigSequence : ConfigChild(), Iterable<Any?>, ConfigCopyable {

    private val _seq = ArrayList<Any?>()
    val list get() = ArrayList(_seq) // returns a new copy every time

    val size get() = _seq.size
    fun isEmpty() = _seq.isEmpty()
    val lastIndex get() = _seq.lastIndex
    companion object Static {

        @JvmStatic
        fun from(arr: Iterable<Any?>): ConfigSequence {
            val seq = ConfigSequence()
            arr.forEach { seq.add(it) }
            return seq
        }

    }

    operator fun Any?.unaryPlus() {
        add(this)
    }

    @JvmOverloads
    open fun add(value: Any?, index: Int = _seq.size) {
        val v = Configs.getValue(value)
        if (v is ConfigChild) v.parentInternal = this
        _seq.add(v)
    }

    @JvmOverloads
    open fun addAll(values: Iterable<Any?>, index: Int = _seq.size) {
        var i = index
        values.forEach { add(it, i); i++ }
    }

    operator fun get(index: Int) = _seq[index]
    operator fun set(index: Int, value: Any?) {
        _seq[index] = value
    }

    inline fun <reified T> get(def: T, index: Int): T = getOrNull<T>(index) ?: def
    inline fun <reified T> getOrNull(index: Int): T? {
        val v = get(index)
        return if (v is T) v else null
    }

    @JvmOverloads
    fun getNumber(def: Number = 0, index: Int) = get(def, index)

    @JvmOverloads
    fun getString(def: String = "", index: Int) = get(def, index)

    @JvmOverloads
    fun getBoolean(def: Boolean = false, index: Int) = get(def, index)

    @JvmOverloads
    fun getSequence(def: ConfigSequence = ConfigSequence(), index: Int) = get(def, index)

    @JvmOverloads
    fun getSection(def: ConfigSection = ConfigSection(), index: Int) = get(def, index)

    fun toList(): ArrayList<Any?> {
        val list = ArrayList<Any?>()
        _seq.forEach { list.add(Configs.getValue(it)) }
        return list
    }

    override fun iterator(): Iterator<Any?> = _seq.iterator()

    override fun equals(other: Any?): Boolean = if(other is ConfigSequence) equals(other) else super.equals(other)

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