// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles

import com.rcode3.vertx_kotlin.EXAMPLE_JSON_ADDR
import com.rcode3.vertx_kotlin.JSON_PROP_COUNT
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.Counter

class ExampleJSONReceiver : AbstractVerticle() {

    override fun start(startFuture: Future<Void> ) {

        lateinit var counter : Counter
        vertx.sharedData().getCounter( "my.counter" ) {
            if( it.succeeded() ) {
                counter = it.result()
            }
            else {
                println( "unable to get counter" )
                startFuture.failed()
            }
        }

        // get the event bus
        val eb = vertx.eventBus()

        // register a consumer that handles strings
        eb.consumer<String>( EXAMPLE_JSON_ADDR ) { message ->

            // the message is JSON. make the JSON usable
            val json = JsonObject( message.body() )
            println( "received json: $json")

            // increment and get the counter
            counter.incrementAndGet {

                if( it.succeeded() ) {

                    // change the JSON and send it back as a reply
                    message.reply( json.put( JSON_PROP_COUNT, it.result() ).toString() )
                }

                else {
                    println( "error getting counter" )
                }
            }
        }

        startFuture.complete()
    }
}