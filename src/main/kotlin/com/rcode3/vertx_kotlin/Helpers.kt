// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin

import io.reactiverse.pgclient.PgPoolOptions
import io.reactiverse.reactivex.pgclient.PgClient
import io.reactiverse.reactivex.pgclient.PgPool
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

/**
 * Gets an Rx version of [io.reactiverse.pgclient.PgPool] with a non-Rx verstion of Vertx reference.
 */
fun getRxPgClient( vertx: Vertx, dbConfig : JsonObject ) : PgPool {
    val rxVertx = io.vertx.reactivex.core.Vertx( vertx )
    return PgClient.pool( rxVertx, PgPoolOptions( dbConfig ) )
}
