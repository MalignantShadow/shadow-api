package info.malignantshadow.api.db

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

open class QueryResultRow(private val data: Map<String, Any?>) {

    fun asString(column: String) = get(column)?.toString() ?: "null"

    fun isNumber(column: String) = get(column) is Number

    fun asNumber(column: String) = get(column) as Number

    fun asBoolean(column: String): Boolean {
        val it = get(column)
        when(it) {
            is Boolean -> return it
            is Number -> return it.toInt() == 1
            is String -> return it == "1" || it.equals("true", true)
        }
        return false
    }

    fun asTimestamp(column: String, zone: ZoneOffset = ZoneOffset.UTC): LocalDateTime? {
        val it = get(column)
        when(it) {
            is String -> return LocalDateTime.parse(column)
            is Number -> return LocalDateTime.ofEpochSecond(it.toLong(), 0, zone)
            is Date -> return LocalDateTime.ofEpochSecond(it.time, 0, zone)
        }
        return null
    }

    operator fun get(column: String) = data[column]

    operator fun contains(column: String): Boolean = column in data

}