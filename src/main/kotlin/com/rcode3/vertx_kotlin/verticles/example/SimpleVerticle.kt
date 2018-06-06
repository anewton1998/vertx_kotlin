// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.example

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

/**
 * This verticle just demonstrates what is needed to create a verticle.
 */
class SimpleVerticle : AbstractVerticle() {

    override fun start( startFuture: Future<Void> ) {
        println( "simple verticle started" )
        startFuture.complete()
    }

    override fun stop( stopFuture: Future<Void> ) {
        println( "simple verticle stopping" )
        stopFuture.complete()
    }
}