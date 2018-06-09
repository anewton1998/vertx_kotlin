// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.example

import com.markodevcic.kvalidation.ValidatorBase
import com.markodevcic.kvalidation.rules
import com.rcode3.vertx_kotlin.VALIDATOR_ADDR
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.get
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

/**
 * This verticle demonstrates validation JSON.
 * This verticle validates a mythical user (in JSON).
 *
 * Note that unlike [ExampleJSONReceiver] and [ExampleJSONSender], it accepts [JsonObject].
 */
class Validator : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {

        // get the event bus
        val eb = vertx.eventBus()

        eb.consumer<JsonObject>( VALIDATOR_ADDR ) { message ->

            val result = validateUser( message.body() )
            if( result.isEmpty ) {
                message.reply( result )
            }
            else {
                message.fail( 0, result.toString() )
            }

        }

        startFuture.complete()
    }

    /**
     * Takes in a [JsonObject] with user data.
     * Return a [JsonObject] with validation errors.
     */
    fun validateUser( user : JsonObject ) : JsonObject {

        val validator = JsonValidator( user )

        validator.forProperty { it.getString( "firstName" ) } rules {
            length( 4 )
        }

        validator.forProperty { it.getString( "lastName" ) } rules {
            length( 4 )
        }

        validator.forProperty { it.getInteger( "age" ) } rules {
            gte( 18 )
        }

        // validate
        val result = validator.validate()

        // get the errors
        val retval : JsonObject = if( !result.isValid ) {
            json {
                obj(
                        "errors" to array {
                            for (error in result.validationErrors) {
                                add(error.toString())
                            }
                        }
                )
            }
        }
        else {
            json { obj() }
        }

        return retval
    }

    class JsonValidator( jsonObject: JsonObject ) : ValidatorBase<JsonObject>( jsonObject ) {}

}