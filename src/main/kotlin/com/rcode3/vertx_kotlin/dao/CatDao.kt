//Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.dao

import io.reactiverse.reactivex.pgclient.PgConnection
import io.reactiverse.reactivex.pgclient.Row
import io.reactiverse.reactivex.pgclient.Tuple
import io.reactivex.Single
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class CatDao {

    companion object {
        const val TABLE_NAME = "cat"
    }

    enum class Column(val columnName : String ){
        NAME( "name" ),
        TYPE( "type" );
        override fun toString() : String {
            return columnName
        }
    }

    val selectAll = "select ${Column.NAME}, ${Column.TYPE} from $TABLE_NAME"
    val selectAllLimited = "select ${Column.NAME}, ${Column.TYPE} from $TABLE_NAME limit $1"
    val selectCount = "select count(*) from $TABLE_NAME"

    fun all( connection: Single<PgConnection>) : Single<JsonArray> {
        return connection.flatMap { conn ->
            conn.rxQuery( selectAll )
                    .map{ rowset ->
                        val retval = JsonArray()
                        for( row in rowset ) {
                            val cat = jsonObject(row)
                            retval.add( cat )
                        }
                        retval
                    }
                    .doFinally { conn.close() }
        }
    }

    fun allLimited(connection: Single<PgConnection>, limit: Long ) : Single<JsonArray> {
        return connection.flatMap { conn ->
            conn.rxPreparedQuery( selectAllLimited, Tuple.of( limit ) )
                    .map { rowset ->
                        val retval = JsonArray()
                        for( row in rowset ) {
                            val cat = jsonObject(row)
                            retval.add( cat )
                        }
                        retval
                    }
                    .doFinally { conn.close() }
        }
    }

    fun count( connection: Single<PgConnection> ) : Single<Int> {
        return connection.flatMap { conn ->
            conn.rxQuery( selectCount )
                    .map { rowset ->
                        var retval = 0
                        for( row in rowset ) {
                            retval = row.getInteger( 0 )
                        }
                        retval
                    }
                    .doFinally { conn.close() }
        }

    }

    private fun jsonObject(row: Row): JsonObject {
        val cat = json {
            obj(
                    "name" to row.getString(0),
                    "type" to row.getString(1)
            )
        }
        return cat
    }

}