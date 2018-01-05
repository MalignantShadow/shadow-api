package info.malignantshadow.api.db

import java.sql.ResultSet

open class QueryResult(private val resultSet: ResultSet) : Iterable<QueryResultRow> {

    private var _result: ArrayList<QueryResultRow> = ArrayList()
    private var columns: Array<String> = Array(resultSet.metaData.columnCount) { "" }
    val size get() = _result.size
    val isEmpty get() = _result.isEmpty()

    init {
        for(i in 0 until columns.size) columns[0] = resultSet.metaData.getCatalogName(i)
        while(resultSet.next()) {
            val data = HashMap<String, Any?>()
            columns.forEach { data[it] = resultSet.getObject(it) }
            _result.add(QueryResultRow(data))
        }
    }

    operator fun get(index: Int): Any? = _result.getOrNull(index)

    operator fun contains(column: String): Boolean = column in columns

    override fun iterator(): Iterator<QueryResultRow> = _result.iterator()

}