// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.example

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.Counter
import mu.KLogging

/**
 * This verticle receives JSON text from [ExampleJSONSender], and modifies it with a counter
 * and sends the JSON text back.
 *
 * The counter is using the Vertx shared data, so technically it could be accessed by other
 * verticles if necessary.
 *
 * The received JSON has a count with a value of null. Once received, the JSON is modified
 * and sent back with an actual value in the JSON.
 *
 */
class ExampleJSONReceiver : AbstractVerticle() {

    companion object : KLogging()

    override fun start(startFuture: Future<Void> ) {

        // gets the reference to the shared data
        lateinit var counter : Counter
        // get a reference to the counter
        vertx.sharedData().getCounter( "my.counter" ) {
            if( it.succeeded() ) {
                counter = it.result()
            }
            else {
                logger.error {  "unable to get counter" }
                startFuture.failed()
            }
        }

        // get the event bus
        val eb = vertx.eventBus()

        // register a consumer that handles strings
        eb.consumer<String>(EXAMPLE_JSON_ADDR) { message ->

            // the message is JSON. make the JSON usable
            val json = JsonObject( message.body() )
            logger.info( "received json: $json" )

            // increment and get the counter
            counter.incrementAndGet {

                if( it.succeeded() ) {

                    // change the JSON and send it back as a reply
                    message.reply( json.put(JSON_PROP_COUNT, it.result() ).toString() )
                }

                else {
                    logger.error( "error getting counter" )
                }
            }
        }

        startFuture.complete()
    }
}

/**
 * Event bus address for the Example JSON Verticles
 */
const val EXAMPLE_JSON_ADDR = "example.json"
/**
 * JSON property name for count
 */
const val JSON_PROP_COUNT = "count"