// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeAll
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

        // This is for testing. Shouldn't be done in vertx as it is a blocking operation
        val resource = System::class.java.getResource( "/sample-config.json" ).readText()
        val json = JsonObject( resource )
        val options = DeploymentOptions().setConfig( json )

        vertx.deployVerticle( Main(), options,
                testContext.succeeding{
                    testContext.completeNow()
                }
        )

    }

}