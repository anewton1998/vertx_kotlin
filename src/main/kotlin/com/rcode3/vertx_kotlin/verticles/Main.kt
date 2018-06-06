// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles

import com.rcode3.vertx_kotlin.PERIODIC_TIMER_ADDR
import com.rcode3.vertx_kotlin.verticles.example.ExampleJSONReceiver
import com.rcode3.vertx_kotlin.verticles.example.ExampleJSONSender
import com.rcode3.vertx_kotlin.verticles.example.SimpleVerticle
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Future

/**
 * This is a "Main" verticle. It is used to deploy all other verticles.
 * This verticle is deployed by the Vertx [io.vertx.core.Launcher] class.
 */
class Main : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {

        println( "hello world" )
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
            println( "Deployment of ${result.result()} succeeded")
        }
        else {
            println( "Deployment of ${result.result()} failed")
            vertx.close()
        }
    }
}