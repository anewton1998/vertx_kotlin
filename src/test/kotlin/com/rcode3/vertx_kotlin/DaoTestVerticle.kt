// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin

import io.reactiverse.pgclient.PgPoolOptions
import io.reactiverse.reactivex.pgclient.PgClient
import io.reactiverse.reactivex.pgclient.PgPool
import io.reactivex.disposables.Disposable
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle

class DaoTestVerticle( val dbConfig : JsonObject, val test : ( PgPool ) -> Disposable ) : AbstractVerticle() {
    override fun start() {
        val client = PgClient.pool( vertx, PgPoolOptions( dbConfig ) )
        test( client )
    }
}
