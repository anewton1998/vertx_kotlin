// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.example

import com.rcode3.vertx_kotlin.EXAMPLE_JSON_ADDR
import com.rcode3.vertx_kotlin.JSON_PROP_COUNT
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

class ExampleJSONReceiverTest : ShouldSpec() {

    val vertx = Vertx.vertx()

    override fun beforeSpec(description: Description, spec: Spec) {
        vertx.deployVerticle( ExampleJSONReceiver::class.java.name )
    }

    override fun afterSpec(description: Description, spec: Spec) {
        vertx.close()
    }

    init {

        val json = JsonObject().putNull( JSON_PROP_COUNT )
        vertx.eventBus().send<String>( EXAMPLE_JSON_ADDR, json.toString() ) { ar ->
            if( ar.succeeded() )
            {
                should("this test work") {
                    val jsonResult = JsonObject(ar.result().body())
                    jsonResult.getInteger(JSON_PROP_COUNT) shouldBe 100
                }
            }
            else {
                fail( "something bad happened" )
            }
        }

/*        should("this test work") {
            val json = JsonObject().putNull( JSON_PROP_COUNT )
            vertx.eventBus().send<String>( EXAMPLE_JSON_ADDR, json.toString() ) { ar ->
                val jsonResult = JsonObject( ar.result().body() )
                jsonResult.getInteger( JSON_PROP_COUNT ) shouldBe 100
            }
        }*/

    }

}
