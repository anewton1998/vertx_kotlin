// Copyright(C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.db

import io.reactiverse.pgclient.PgClient
import io.reactiverse.pgclient.PgPoolOptions
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

class PgInitVerify : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {

        var options = PgPoolOptions( config() )

        // Create the client pool
        var client = PgClient.pool(vertx, options)

        // A simple query
        client.query("SELECT * FROM person WHERE name='julien'") { ar ->
            if (ar.succeeded()) {
                var result = ar.result()
                println("Got ${result.size()} rows ")
                startFuture.complete()
            } else {
                println("Failure: ${ar.cause().message}")
                startFuture.fail( ar.cause() )
            }

            // Now close the pool
            client.close()
        }



    }


}