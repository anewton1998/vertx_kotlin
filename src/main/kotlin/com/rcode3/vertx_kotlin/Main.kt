// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin

import com.rcode3.vertx_kotlin.verticles.example.ExampleJSONReceiver
import com.rcode3.vertx_kotlin.verticles.example.ExampleJSONSender
import com.rcode3.vertx_kotlin.verticles.example.SimpleVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx

/**
 * This is an example of starting vertx and deploying verticles from a typical main method.
 *
 * To do this, the jar must have this class listed in the manifest as Main-Class
 */

fun main( args: Array<String> ) {
    println( "hello world" )
    val vertx = Vertx.vertx()
    vertx.deployVerticle( SimpleVerticle::class.java.name ) { handleVerticleDeployment( it ) }
    vertx.deployVerticle( ExampleJSONReceiver::class.java.name ) { handleVerticleDeployment( it ) }
    vertx.deployVerticle( ExampleJSONSender::class.java.name ) { handleVerticleDeployment( it ) }
    vertx.setPeriodic( 2000 ) {
        vertx.eventBus().publish( PERIODIC_TIMER_ADDR, null )
    }
}

fun handleVerticleDeployment(result: AsyncResult<String> ) {
    if( result.succeeded() ) {
        println( "Deployment of ${result.result()} succeeded")
    }
    else {
        println( "Deployment of ${result.result()} failed")
        Vertx.vertx().close()
    }
}
