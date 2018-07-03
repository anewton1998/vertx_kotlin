// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin

import io.reactiverse.pgclient.PgPoolOptions
import io.reactiverse.reactivex.pgclient.PgClient
import io.reactiverse.reactivex.pgclient.PgPool
import io.reactivex.Single
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import java.util.concurrent.TimeUnit

class DaoTestVerticle( val dbConfig : JsonObject, val test : ( PgPool ) -> Single<Unit>) : AbstractVerticle() {
    override fun start(startFuture : Future<Void>) {
        val client = PgClient.pool( vertx, PgPoolOptions( dbConfig ) )
        test( client )
                .subscribe(
                        { t -> startFuture.complete() },
                        { startFuture.fail( "assertions and such" ) }
                )
    }
}
