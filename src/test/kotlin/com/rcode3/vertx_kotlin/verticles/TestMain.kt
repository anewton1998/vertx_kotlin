// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName( "Test of Main Veritcle" )
@ExtendWith( VertxExtension::class )
object TestMain {

    @DisplayName( "Main Verticle Deploys all other Verticles" )
    @Test
    fun testMainVerticle( vertx : Vertx, testContext: VertxTestContext ) {

        // Normally this would be in a prepare function, but the thing
        // actually being tested is the deployment of the other verticles

        vertx.deployVerticle( Main(),
                testContext.succeeding{
                    testContext.completeNow()
                }
        )

    }

}