package info.malignantshadow.api.util.random

import java.util.Random

open class WeightedRandom<T> {

    private var _weights: HashMap<T, Int> = HashMap()
    private var _r: Random = Random()
    private var _total = 0

    fun add(element: T, weight: Int = 1) {
        require(weight > 0) { "Weight cannot be <= 0" }
        _weights.put(element, weight)
        _total += weight
    }

    operator fun set(element: T, weight: Int) {

    }

    fun next(): T? {
        if (_total == 0) return null

        val roll = _r.nextInt(_total) + 1
        var total = 0
        var last: T? = null
        for (pair in _weights) {
            last = pair.key
            val nextTotal = total + pair.value
            if (roll in total + 1..nextTotal) break
            total = nextTotal
        }
        return last
    }

}