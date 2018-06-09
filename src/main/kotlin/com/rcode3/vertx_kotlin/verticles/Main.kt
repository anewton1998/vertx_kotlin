// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles

import com.rcode3.vertx_kotlin.PERIODIC_TIMER_ADDR
import com.rcode3.vertx_kotlin.handleVerticleDeployment
import com.rcode3.vertx_kotlin.verticles.example.ExampleJSONReceiver
import com.rcode3.vertx_kotlin.verticles.example.ExampleJSONSender
import com.rcode3.vertx_kotlin.verticles.example.SimpleVerticle
import com.rcode3.vertx_kotlin.verticles.example.Validator
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import mu.KLogging

/**
 * This is a "Main" verticle. It is used to deploy all other verticles.
 * This verticle is deployed by the Vertx [io.vertx.core.Launcher] class.
 */
class Main : AbstractVerticle() {

    companion object : KLogging()

    override fun start(startFuture: Future<Void>) {

        logger.debug( "hello world" )

        // The CompositeFuture is used to coordinate the deployment of all the verticles
        // using a future from each.
        //
        // Once all the verticles are deployed, it sets a periodic timer
        CompositeFuture.all(
                listOf(
                        deployVerticle( SimpleVerticle() ),
                        deployVerticle( ExampleJSONSender() ),
                        deployVerticle( ExampleJSONReceiver() ),
                        deployVerticle( Validator() )
                )
        ).setHandler{ ar ->
            if( ar.succeeded() ) {
                vertx.setPeriodic( 2000 ) {
                    vertx.eventBus().publish(PERIODIC_TIMER_ADDR, null)
                }
                startFuture.complete()
            }
            else {
                startFuture.fail( "one of the verticles failed to deploy" )
            }
        }

    }

    /**
     * This creates a future and deploys the verticle, using [handleVerticleDeployment] as the handler.
     *
     * @return the future
     */
    fun deployVerticle( verticle: AbstractVerticle ) : Future<String> {
        val future = Future.future<String>()
        future.setHandler { handleVerticleDeployment( it ) }
        vertx.deployVerticle( verticle, future.completer() )
        return future
    }

    /**
     * Does something with the deployment of the verticle.
     */
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