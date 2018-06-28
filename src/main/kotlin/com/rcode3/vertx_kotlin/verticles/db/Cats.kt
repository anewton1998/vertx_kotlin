// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.db

import com.rcode3.vertx_kotlin.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import mu.KLogging

/**
 * This verticle is a DAO for the Cats.
 */
class Cats : AbstractVerticle() {

    companion object : KLogging()

    override fun start( startFuture: Future<Void>) {

        // get the event bus
        val eb = vertx.eventBus()

        //register consumer
        eb.consumer<JsonObject>( CATS_DAO ) {
            when ( it.headers()[ DB_ACTION ] ) {
                ENTITY_ALL -> {}
                ENTITY_COUNT -> {}
                ENTITY_DELETE -> {}
                ENTITY_SAVE -> {}
                ENTITY_GET -> {}
                else -> {
                    it.fail( NOT_IMPLEMENTED, "unknown action ${it.headers()[DB_ACTION]} for $CATS_DAO")
                }
            }
        }
    }
}

const val CATS_DAO = "db.cats"