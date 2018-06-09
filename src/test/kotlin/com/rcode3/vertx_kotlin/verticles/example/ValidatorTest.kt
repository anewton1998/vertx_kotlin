// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.example

import io.vertx.core.Vertx
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
    fun testValidateUser( vertx: Vertx, testContext: VertxTestContext ) {

        // setup the JSON object to test
        val json = json{
            obj(
                    "firstName" to "Bob",
                    "lastName"  to "Smurd",
                    "age" to 18
            )
        }

        // create a validator verticle
        val validator = Validator()

        // validate the json. it should not have any errors.
        var result = validator.validateUser( json )
        assertThat( result.isEmpty )

        // now change the age to 16 and validate again
        // it should have errors in the result json
        json.put( "age", 16 )
        result = validator.validateUser( json )
        assertThat( !result.isEmpty )

        testContext.completeNow()
    }
}