// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.example

import com.rcode3.vertx_kotlin.CONFIG_DB
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import mu.KLogging

/**
 * This verticle shows a simple example of getting configuration
 */
class Configured : AbstractVerticle() {

    companion object : KLogging()

    override fun start(startFuture: Future<Void>) {

        logger.info( "Configuration Example Verticle" )

        val db : JsonObject? = config().getJsonObject( CONFIG_DB )

        db?.let{
            logger.info( "url: ${db.getString( "url" )}")
            logger.info( "driver_class: ${db.getString( "driver_class" )}")
            logger.info( "max_pool_size: ${db.getInteger( "max_pool_size" )}")
            logger.info( "user: ${db.getString( "user" )}")
            logger.info( "password: ${db.getString( "password" )}")
        } ?: run {
            logger.error( "no database configuration available" )
        }

        // alternatively, we could just do this
        db?.let {
            logger.info( "database configuration: $db")
        } ?: run {
            logger.error( "database configuration not found" )
        }

        startFuture.complete()
    }
}