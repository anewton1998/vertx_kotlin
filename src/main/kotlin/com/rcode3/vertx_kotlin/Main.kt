// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin

import com.rcode3.vertx_kotlin.verticles.ExampleJSONReceiver
import com.rcode3.vertx_kotlin.verticles.ExampleJSONSender
import com.rcode3.vertx_kotlin.verticles.SimpleVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx

fun main( args: Array<String> ) {
    println( "hello world" )
    val vertx = Vertx.vertx()
    vertx.deployVerticle( SimpleVerticle::class.java.name ) { handleVerticleDeployment( it ) }
    vertx.deployVerticle( ExampleJSONReceiver::class.java.name ) { handleVerticleDeployment( it ) }
    vertx.deployVerticle( ExampleJSONSender::class.java.name ) { handleVerticleDeployment( it ) }
    vertx.setPeriodic( 1000 ) {
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
