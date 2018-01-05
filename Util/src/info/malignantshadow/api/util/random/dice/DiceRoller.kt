package info.malignantshadow.api.util.random.dice

import info.malignantshadow.api.util.aliases.Aliasable

class DiceRoller(var dice: ArrayList<Die> = arrayListOf(Die())) {

    enum class Rule {
        REROLL, RRMAX, ROUND, EXPLODE, EMAX, COUNT, RADD("r+"), RSUB("r-"), RMUL("r*"), RDIV("r/");

        private var _id: String?
        val id: String get() = _id ?: this.name.toLowerCase()

        constructor(id: String? = null) {
            _id = id
        }

        companion object {

            @JvmStatic
            fun getRule(id: String): Rule? {
                values().forEach { if (it.id == id) return@getRule it }
                return null
            }

        }
    }

    enum class Selector(val fn: (Array<Die>, Int) -> List<Die>, override val aliases: List<String> = listOf()) : Aliasable {
        ALL({ dice, _ -> dice.toList() }),
        HIGHEST(selectBound(), listOf("h", "high", "highest")),
        LOWEST(selectBound(false), listOf("l", "low", "lowest"));

        companion object {

            @JvmStatic
            fun getSelector(name: String): Selector? {
                values().forEach { if (name.toUpperCase() == it.name || name.toLowerCase() in it.aliases) return@getSelector it }
                return null
            }
        }
    }

    companion object {

        private val DICE_REGEX = """[dD]\d+"""
        private val FULL_DICE_REGEX = """(\d+($DICE_REGEX)?|$DICE_REGEX)"""
        private val SEL_REGEX = """@\w+\d*"""
        private val RULES_REGEX = """\s+[\w+-/*]+(=\d+(,\d+)*)?"""
        private val REGEX = "$FULL_DICE_REGEX(,$FULL_DICE_REGEX)*($SEL_REGEX)?($RULES_REGEX)*"

        @JvmStatic
        fun compile(s: String?): DiceRoller? { // 4d6@selector rule=arg,arg
            if (s == null || !s.matches(Regex(REGEX))) return null
            val split = s.split(Regex("\\s+"))
            val diceAndSelector = split[0].split(Regex("@"))
            val diceBagInput = diceAndSelector[0].split(Regex(","))
            val diceBag = ArrayList<Die>()
            val dReg = Regex("[dD]")
            diceBagInput.forEach {
                if (dReg in it) { // #?d#
                    val split = it.split(Regex("[dD]"))
                    diceBag.addAll(
                            if (split[0] == "") listOf(Die(split[1].toInt())) // d#
                            else List(split[0].toInt()) { Die(split[1].toInt()) } // #d#
                    )
                } else diceBag.addAll(List(split[0].toInt()) { Die() })
            }
            diceBag.sortBy { it.shownFace }

            val selectorString = diceAndSelector.getOrNull(1)
            var sel: Pair<Selector, Int>? = null
            if (selectorString != null) {
                val index = selectorString.indexOfFirst { it.isDigit() }
                if (index > -1) {
                    val selector = Selector.getSelector(selectorString.substring(0, index))
                    if (selector != null) sel = selector to selectorString.substring(index).toInt()
                }
            }

            val roller = DiceRoller(diceBag)
            if (sel != null) roller.selector = sel

            val rules: HashMap<Rule, ArrayList<Int>?> = HashMap()
            if (split.size > 1) split.subList(1, split.size).forEach {
                val ruleAndArgs = it.split(Regex("="))
                val rule = Rule.getRule(ruleAndArgs[0])
                val mapped = ruleAndArgs.getOrNull(1)
                        ?.split(Regex(","))
                        ?.map { it.toInt() }
                val args = if (mapped != null) ArrayList(mapped) else null
                if (rule != null) rules[rule] = args
            }
            roller.rules = rules

            return roller
        }

        private fun selectBound(asc: Boolean = true): (Array<Die>, Int) -> List<Die> {
            return { dice, amount ->
                var copy = dice.toList().sortedBy { it.shownFace }
                if (copy.isEmpty()) copy
                if (!asc) copy = copy.reversed()
                copy.subList(0, Math.min(amount, copy.size))
            }
        }


    }

    private var _sum: Int = 0
    val sum: Int get() = _sum

    var rules: HashMap<Rule, ArrayList<Int>?> = HashMap()
    var selector: Pair<Selector, Int> = Selector.ALL to 1
    private var _rerollsLeft = 0
    private var _explodesLeft = 0

    private var _result = 0
    val result get() = _result

    private var _count = HashMap<Int, Int>()
    val count get() = _count

    val output: String
        get() {
            val out = if (Rule.COUNT in rules)
                "| " + _count.toSortedMap(compareBy { it })
                        .filter { (k, _) ->
                            rules[Rule.COUNT] == null || rules[Rule.COUNT]?.contains(k) == true
                        }
                        .map { (k, v) -> "(${k}s: $v)" }
                        .joinToString(", ")
            else ""
            return "${toString()} => $sum $out".trim()
        }

    fun setAttr(attr: Rule, vararg values: Int) {
        rules[attr] = ArrayList(values.toList())
    }

    fun addAttr(attr: Rule, vararg values: Int) {
        setAttr(attr)
        rules[attr]?.addAll(values.toList())
    }

    private fun roll(d: Die) {
        d.roll()
        _count[d.shownFace] = _count[d.shownFace]?.plus(1) ?: 1
        if (_rerollsLeft > 0 && rules[Rule.REROLL]?.contains(d.shownFace) == true) {
            _rerollsLeft--
            roll(d)
            return
        }

        if (_explodesLeft > 0 && rules[Rule.EXPLODE]?.contains(d.shownFace) == true) {
            _explodesLeft--
            _result += d.shownFace
            roll(d)
            return
        }

        val round = rules[Rule.ROUND]
        if (round != null) {
            round.sort()
            for (i in round) {
                if (d.shownFace < i) {
                    d.shownFace = i
                    break
                }
            }
        }

        _result += d.shownFace
    }

    fun roll() {
        _count.clear()
        _rerollsLeft = rules[Rule.RRMAX]?.getOrNull(0) ?: 0
        _explodesLeft = rules[Rule.EMAX]?.getOrNull(0) ?: 0
        _sum = 0
        dice.forEach { roll(it); _sum += it.shownFace }

        //modify result: multiply, divide, add, subtract
        _sum *= rules[Rule.RMUL]?.getOrNull(0) ?: 1
        _sum /= rules[Rule.RDIV]?.getOrNull(0) ?: 1
        _sum += rules[Rule.RADD]?.getOrNull(0) ?: 0
        _sum -= rules[Rule.RSUB]?.getOrNull(0) ?: 0

    }

    fun range(): IntRange {
        val min = dice.size
        var max = 0
        dice.forEach { max += it.faces }
        return min..max
    }

    override fun toString(): String {
        val diceString = dice.groupBy { it.faces }.map { (d, a) -> "${a.size}d$d" }.joinToString(",")
        val selString =
                if (selector.first != Selector.ALL)
                    "@${selector.first.aliases[0].toUpperCase()}${selector.second}"
                else ""
        val rulesString = rules
                .map { (k, v) -> "${k.id}${if (v != null) "=${v.joinToString(",")}" else ""}" }
                .joinToString(" ")
        return "$diceString$selString $rulesString".trim()
    }
}