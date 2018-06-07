// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.example

import com.rcode3.vertx_kotlin.EXAMPLE_JSON_ADDR
import com.rcode3.vertx_kotlin.JSON_PROP_COUNT
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@DisplayName( "Test of ExampleJSONReceiver" )
@ExtendWith( VertxExtension::class )
object ExampleJSONReceiverTest {

    @BeforeAll
    @DisplayName( "Deploy ExampleJSONReceiver Verticle" )
    @JvmStatic
    fun prepare(vertx: Vertx, testContext: VertxTestContext ) {
        vertx.deployVerticle( ExampleJSONReceiver::class.java.name,
                testContext.succeeding{ testContext.completeNow() } )
    }

    @DisplayName( "JSON property is replaced" )
    @Test
    fun testJsonProp( vertx: Vertx, testContext: VertxTestContext ) {

        val json = JsonObject().putNull( JSON_PROP_COUNT )
        vertx.eventBus().send<String>( EXAMPLE_JSON_ADDR, json.toString(),
                testContext.succeeding{
                    testContext.verify{
                        val jsonResult = JsonObject( it.body() )
                        assertThat( jsonResult.getInteger( JSON_PROP_COUNT) ).isInstanceOf( Integer::class.java )
                        testContext.completeNow()
                    }
                } )
    }

}
