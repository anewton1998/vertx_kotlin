// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles

import com.rcode3.vertx_kotlin.PERIODIC_TIMER_ADDR
import com.rcode3.vertx_kotlin.verticles.example.ExampleJSONReceiver
import com.rcode3.vertx_kotlin.verticles.example.ExampleJSONSender
import com.rcode3.vertx_kotlin.verticles.example.SimpleVerticle
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import mu.KLogging

/**
 * This is a "Main" verticle. It is used to deploy all other verticles.
 * This verticle is deployed by the Vertx [io.vertx.core.Launcher] class.
 */
class Main : AbstractVerticle() {

    companion object : KLogging()

    override fun start(startFuture: Future<Void>) {

        println( "jul logging config ${System.getProperty("java.util.logging.config.file")} ")

        logger.debug( "hello world" )
        vertx.deployVerticle( SimpleVerticle::class.java.name ) { handleVerticleDeployment( it ) }
        vertx.deployVerticle( ExampleJSONReceiver::class.java.name ) { handleVerticleDeployment( it ) }
        vertx.deployVerticle( ExampleJSONSender::class.java.name ) { handleVerticleDeployment( it ) }
        vertx.setPeriodic( 2000 ) {
            vertx.eventBus().publish( PERIODIC_TIMER_ADDR, null )
        }

        startFuture.complete()
    }

    fun handleVerticleDeployment(result: AsyncResult<String>) {
        if( result.succeeded() ) {
            logger.debug{ "Deployment of ${result.result()} succeeded" }
        }
        else {
            logger.error{ "Deployment of ${result.result()} failed" }
            vertx.close()
        }
    }
}