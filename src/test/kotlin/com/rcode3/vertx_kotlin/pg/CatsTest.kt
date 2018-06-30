// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.pg

import com.rcode3.vertx_kotlin.InitPg
import io.reactiverse.pgclient.PgPoolOptions
import io.reactiverse.reactivex.pgclient.PgClient
import io.vertx.reactivex.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName( "Cats DB Tests" )
@ExtendWith( VertxExtension::class )
object CatsTest {

    var dbConfig = JsonObject()

    @DisplayName( "Prepare the database" )
    @BeforeAll
    @JvmStatic
    fun prepare(vertx: io.vertx.core.Vertx, testContext: VertxTestContext) {
        dbConfig = InitPg.startPg()
        testContext.completeNow()
    }

    @DisplayName( "Get all cats" )
    @Test
    fun testAll() {
        val testContext = VertxTestContext()
        val vertx = Vertx.vertx()
        val client = PgClient.pool( vertx, PgPoolOptions( dbConfig ) )
        Cats().all( client.rxGetConnection() )
            .doAfterTerminate {
              client.close()
              testContext.completeNow()
            }
                .subscribe { jsonArray ->
                    println( jsonArray )
                    assertThat( jsonArray.isEmpty ).isFalse()
                }

    }
}
