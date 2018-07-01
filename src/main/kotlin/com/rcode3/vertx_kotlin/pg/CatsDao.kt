//Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.pg

import io.reactiverse.reactivex.pgclient.PgConnection
import io.reactiverse.reactivex.pgclient.Row
import io.reactiverse.reactivex.pgclient.Tuple
import io.reactivex.Single
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class CatsDao {

    val selectAll = "select name, type from cats"
    val selectAllLimited = "select name, type from cats limit ?"

    fun all( connection: Single<PgConnection>) : Single<JsonArray> {
        return connection.flatMap { conn ->
            conn.rxQuery( selectAll )
                    .doAfterTerminate { conn.close() }
        }
                .map { rowset ->
                    val retval = JsonArray()
                    for( row in rowset ) {
                        val cat = jsonObject(row)
                        retval.add( cat )
                        println( "found cat $cat")
                    }
                    retval
                }
    }

    fun allLimited(connection: Single<PgConnection>, limit: Int ) : Single<JsonArray> {
        return connection.flatMap { conn ->
            conn.rxPreparedQuery( selectAllLimited, Tuple.of( limit ) )
                    .doAfterTerminate { conn.close() }
        }
                .map { rowset ->
                    val retval = JsonArray()
                    for( row in rowset ) {
                        val cat = jsonObject(row)
                        retval.add( cat )
                        println( "found cat $cat")
                    }
                    retval
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