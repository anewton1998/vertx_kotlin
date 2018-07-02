//Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.dao

import com.rcode3.vertx_kotlin.model.Cat
import io.reactiverse.reactivex.pgclient.PgConnection
import io.reactiverse.reactivex.pgclient.Row
import io.reactiverse.reactivex.pgclient.Tuple
import io.reactivex.Single

class CatDao {

    companion object {
        const val TABLE_NAME = "cat"
    }

    enum class Column( val cn : String, val pos : Int ) {
        NAME( "name", 0 ),
        TYPE( "type", 1 )
    }


    val selectAll = "select ${Column.NAME.cn}, ${Column.TYPE.cn} from $TABLE_NAME"
    val selectAllLimited = "select ${Column.NAME.cn}, ${Column.TYPE.cn} from $TABLE_NAME limit $1"
    val selectCount = "select count(*) from $TABLE_NAME"

    fun all( connection: Single<PgConnection>) : Single<List<Cat>> {
        return connection.flatMap { conn ->
            conn.rxQuery( selectAll )
                    .map{ rowset ->
                        val retval = ArrayList<Cat>( rowset.size() )
                        for( row in rowset ) {
                           retval.add( mapRowToCat(row) )
                        }
                        retval
                    }
                    .doFinally { conn.close() }
        }
    }

    fun allLimited(connection: Single<PgConnection>, limit: Long ) : Single<List<Cat>> {
        return connection.flatMap { conn ->
            conn.rxPreparedQuery( selectAllLimited, Tuple.of( limit ) )
                    .map { rowset ->
                        val retval = ArrayList<Cat>( rowset.size() )
                        for( row in rowset ) {
                            retval.add( mapRowToCat( row ) )
                        }
                        retval
                    }
                    .doFinally { conn.close() }
        }
    }

    private fun mapRowToCat(row: Row): Cat {
        return Cat(name = row.getString(Column.NAME.pos),
                type = row.getString(Column.TYPE.pos)
        )
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
}