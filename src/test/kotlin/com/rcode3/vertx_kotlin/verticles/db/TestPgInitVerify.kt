// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.db

import com.rcode3.vertx_kotlin.InitPg
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName( "Test Pg Init" )
@ExtendWith( VertxExtension::class )
object TestPgInitVerify {

    var port = 0

    @DisplayName( "Prepare the database" )
    @BeforeAll
    @JvmStatic
    fun prepare( vertx: Vertx, testContext: VertxTestContext ) {
        port = InitPg.startPg()
        testContext.completeNow()
    }

    @DisplayName( "Test of PgInitVerify verticle")
    @Test
    fun testPgInitVerify( vertx: Vertx, testContext: VertxTestContext ) {
        val dbConfig = json{
            obj(
                    "port" to port,
                    "host" to "localhost",
                    "database" to "postgres",
                    "user" to "postgres",
                    "password" to "secret",
                    "maxSize" to 5
            )
        }
        val options = DeploymentOptions().setConfig( dbConfig )
        vertx.deployVerticle( PgInitVerify(), options, testContext.succeeding{
            testContext.completeNow()
        })
    }

}
