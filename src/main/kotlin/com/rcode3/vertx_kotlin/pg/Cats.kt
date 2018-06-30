//Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.pg

import io.reactiverse.reactivex.pgclient.PgConnection
import io.reactivex.Single
import io.vertx.core.json.JsonArray
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class Cats {

    fun all( connection: Single<PgConnection>) : Single<JsonArray> {
        return connection.flatMap { conn ->
            conn.rxQuery( "select name, type from cats" )
                    .doAfterTerminate { conn.close() }
        }
                .map { rowset ->
                    val retval = JsonArray()
                    for( row in rowset ) {
                        val cat = json {
                            obj(
                                    "name" to row.getString( 0 ),
                                    "type" to row.getString( 1 )
                            )
                        }
                        retval.add( cat )
                        println( "found cat $cat")
                    }
                    retval
                }
    }

}