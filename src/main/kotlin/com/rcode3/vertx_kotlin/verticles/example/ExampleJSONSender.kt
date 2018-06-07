// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.example

import com.rcode3.vertx_kotlin.EXAMPLE_JSON_ADDR
import com.rcode3.vertx_kotlin.JSON_PROP_COUNT
import com.rcode3.vertx_kotlin.PERIODIC_TIMER_ADDR
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import mu.KLogging

/**
 * This is an example of verticle that sends JSON on the event bus.
 *
 * This verticle listens for a timer event. When that timer event
 * goes off, it sends an event with some JSON text.
 *
 * Note that it is sending JSON as actual string text, not as a
 * JSON object that models JSON.
 */
class ExampleJSONSender : AbstractVerticle() {

    companion object : KLogging()

    override fun start(startFuture: Future<Void>) {

        // get the event bus
        val eb = vertx.eventBus()

        // register a consumer for the periodic timer
        eb.consumer<Any>( PERIODIC_TIMER_ADDR ) {

            // when the timer event occurs, send a JSON object
            // this uses the vertx-lang-kotlin json extensions on JsonObject in vertx.core
            val json = json{
                obj(
                    JSON_PROP_COUNT to null
                )
            }

            eb.send<String>( EXAMPLE_JSON_ADDR, json.toString() ) { ar ->

                // get the reply from the sending
                logger.info{ "received reply ${ar.result().body()}" }
            }
        }

        startFuture.complete()
    }
}