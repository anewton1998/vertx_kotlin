// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.db

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName( "Test of DB Init Veritcle" )
@ExtendWith( VertxExtension::class )
object TestInit {

    @DisplayName( "Init Verticle Deploys and initializes the database" )
    @Test
    @Disabled //disabled because this is done in Main.
    fun testMainVerticle( vertx : Vertx, testContext: VertxTestContext ) {

        // Normally this would be in a prepare function, but the thing
        // actually being tested is the deployment of the other verticles

        // This is for testing. Shouldn't be done in vertx as it is a blocking operation
        val resource = System::class.java.getResource( "/sample-config.json" ).readText()
        val json = JsonObject( resource )
        val options = DeploymentOptions().setConfig( json )

        vertx.deployVerticle( Init(), options,
                testContext.succeeding{
                    testContext.completeNow()
                }
        )

    }

}