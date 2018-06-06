// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

class Main : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {

        startFuture.complete()
    }

}