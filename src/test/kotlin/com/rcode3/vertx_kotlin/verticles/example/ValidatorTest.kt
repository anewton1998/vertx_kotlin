// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.example

import com.rcode3.vertx_kotlin.VALIDATOR_ADDR
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName( "Test of the Validator Verticle" )
@ExtendWith( VertxExtension::class )
object ValidatorTest {

    @BeforeAll
    @DisplayName( "Deploy Validator Verticle" )
    @JvmStatic
    fun prepeare( vertx: Vertx, testContext: VertxTestContext ) {
        vertx.deployVerticle( Validator::class.java.name ,
                testContext.succeeding{ testContext.completeNow() } )
    }

    @DisplayName( "Imperative Validation Logic" )
    @Test
    fun testValidateUser() {

        // setup the JSON object to test
        val json = json{
            obj(
                    "firstName" to "Bob",
                    "lastName"  to "Smurd",
                    "age" to 18
            )
        }

        // validate the json. it should not have any errors.
        var result = Validator.validateUser( json )
        assertThat( result.isEmpty )

        // now change the age to 16 and validate again
        // it should have errors in the result json
        json.put( "age", 16 )
        result = Validator.validateUser( json )
        assertThat( !result.isEmpty )
    }

    @DisplayName( "Asynchronouse Validator Logic" )
    @Test
    fun testValidatorVerticle( vertx: Vertx, testContext: VertxTestContext ) {

        // setup the JSON object to test
        val json = json{
            obj(
                    "firstName" to "Bob",
                    "lastName"  to "Smurd",
                    "age" to 18
            )
        }

        val checkpoint = testContext.checkpoint( 2 )

        vertx.eventBus().send<JsonObject>( VALIDATOR_ADDR, json,
                testContext.succeeding{
                    testContext.verify {
                        assertThat( it.headers()[ "validated" ] )
                        assertThat( it.body().isEmpty )
                        checkpoint.flag()
                    }
                }
        )

        json.put( "age", 16 )

        vertx.eventBus().send<JsonObject>( VALIDATOR_ADDR, json,
                testContext.succeeding{
                    testContext.verify {
                        assertThat( it.headers()[ "error" ] )
                        assertThat( ! it.body().isEmpty )
                        checkpoint.flag()
                    }
                }
        )

    }
}