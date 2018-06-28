// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.db

import com.rcode3.vertx_kotlin.*
import io.reactivex.Single
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.sql.ResultSet
import io.vertx.kotlin.core.json.get
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.jdbc.JDBCClient
import io.vertx.reactivex.ext.sql.SQLConnection
import mu.KLogging

/**
 * This verticle is a DAO for the Cats.
 */
class Cats : AbstractVerticle() {

    companion object : KLogging()

    lateinit var client : JDBCClient

    override fun start( startFuture: Future<Void>) {

        client = JDBCClient.createShared( vertx, config()[ CONFIG_DB ] )

        // get the event bus
        val eb = vertx.eventBus()

        //register consumer
        eb.consumer<JsonObject>( CATS_DAO ) {
            when ( it.headers()[ DB_ACTION ] ) {
                ENTITY_ALL -> entityAll()
                ENTITY_COUNT -> {}
                ENTITY_DELETE -> {}
                ENTITY_NEW -> {}
                ENTITY_UPDATE -> {}
                ENTITY_GET -> {}
                else -> {
                    it.fail( NOT_IMPLEMENTED, "unknown action ${it.headers()[DB_ACTION]} for $CATS_DAO")
                }
            }
        }
    }

    fun connect() : Single<SQLConnection> {
        return client.rxGetConnection()
    }

    fun entityAll() : Single<ResultSet> {
        return connect().flatMap { connection ->
            entityAll( connection ).doFinally { connection.close() }
        }
    }

    fun entityAll(connection : SQLConnection) : Single<ResultSet> {
        return connection.rxQuery( "select * from cats" )
    }

}

const val CATS_DAO = "db.cats"