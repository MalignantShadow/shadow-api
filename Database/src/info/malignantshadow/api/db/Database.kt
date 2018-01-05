package info.malignantshadow.api.db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

open class Database(val serverAddress: String,
                    val serverPort: Int,
                    val dbName: String,
                    val dbUser: String,
                    val dbPass: String) {

    private var _conn: Connection? = null
    val conn: Connection? get() = _conn

    fun connect(): Boolean =
            try {
                if (_conn?.isValid(5) == true) _conn = DriverManager.getConnection(String.format(""))
                true
            } catch (e: SQLException) {
                e.printStackTrace()
                false
            }

    private fun prepare(sql: String, params: Array<out Any?>): PreparedStatement? {
        val stmt = _conn?.prepareStatement(sql) ?: return null
        var counter = 1
        params.forEach {
            when (it) {
                is Int -> stmt.setInt(counter++, it)
                is Short -> stmt.setShort(counter++, it)
                is Long -> stmt.setLong(counter++, it)
                is Double -> stmt.setDouble(counter++, it)
                is String -> stmt.setString(counter++, it)
                it == null -> stmt.setNull(counter++, java.sql.Types.NULL)
                else -> stmt.setObject(counter++, it)
            }
        }
        return stmt
    }

    fun write(sql: String, vararg params: Any?) {
        try {
            if(!connect()) return
            prepare(sql, params)?.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun read(sql: String, vararg params: Any?): QueryResult? {
        if(!connect()) return null
        var stmt: PreparedStatement? = null
        var rs: ResultSet? = null
        var result: QueryResult? = null
        try {
            stmt = prepare(sql, params)
            rs = stmt?.executeQuery()
            if(rs != null) result = QueryResult(rs)
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            if(rs != null)
                try {
                    rs.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            if(stmt != null)
                try {
                    stmt.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
        }
        return result
    }
}