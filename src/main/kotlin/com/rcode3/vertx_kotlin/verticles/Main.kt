// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles

import com.rcode3.vertx_kotlin.PERIODIC_TIMER_ADDR
import com.rcode3.vertx_kotlin.handleVerticleDeployment
import com.rcode3.vertx_kotlin.verticles.db.PgInitVerify
import com.rcode3.vertx_kotlin.verticles.example.*
import io.vertx.core.*
import mu.KLogging

/**
 * This is a "Main" verticle. It is used to deploy all other verticles.
 * This verticle is deployed by the Vertx [io.vertx.core.Launcher] class.
 */
class Main : AbstractVerticle() {

    companion object : KLogging()

    override fun start(startFuture: Future<Void>) {

        logger.debug( "hello world" )
        logger.info( "configuration: ${config()}")

        /**
         * Stage1 would be things that initialize resources, or perhaps verify resources are available
         * for a fail-fast start
         */
        val stage1 = listOf(
                SimpleVerticle()
        )

        /**
         * Stage2 are the verticles that are business logice etc...
         */
        val stage2 = listOf(
                ExampleJSONReceiver(),
                ExampleJSONSender(),
                Validator()
        )

        /**
         * Stage3 would bring up the endpoints and things that start "listening" for requests.
         */
        val stage3 = listOf(
                Configured()
        )

        CompositeFuture.all(
                stage1.map{ deployVerticle( it ) }
        ).compose {
            CompositeFuture.all(
                    stage2.map{ deployVerticle( it ) }
            )
        }.compose {
            CompositeFuture.all(
                    stage3.map{ deployVerticle( it ) }
            )
        }.setHandler{ ar ->
            if( ar.succeeded() ) {
                vertx.setPeriodic( 2000 ) {
                    vertx.eventBus().publish(PERIODIC_TIMER_ADDR, null)
                }
                startFuture.complete()
            }
            else {
                startFuture.fail( ar.cause() )
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
        val options = DeploymentOptions().setConfig( config() )
        future.setHandler { handleVerticleDeployment( it ) }
        vertx.deployVerticle( verticle, options, future.completer() )
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
            logger.error( "Deployment of ${result.result()} failed", result.cause() )
        }
    }
}