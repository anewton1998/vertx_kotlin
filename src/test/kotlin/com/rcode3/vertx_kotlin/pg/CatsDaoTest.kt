// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.pg

import com.ninja_squad.dbsetup.DbSetupTracker
import com.ninja_squad.dbsetup_kotlin.dbSetup
import com.ninja_squad.dbsetup_kotlin.launchWith
import com.rcode3.vertx_kotlin.InitPg
import io.reactiverse.pgclient.PgPoolOptions
import io.reactiverse.reactivex.pgclient.PgClient
import io.vertx.reactivex.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName( "CatsDao DB Tests" )
@ExtendWith( VertxExtension::class )
object CatsDaoTest {

    val dbSetupTracker = DbSetupTracker()
    var dbConfig = JsonObject()

    @DisplayName( "Prepare the database" )
    @BeforeAll
    @JvmStatic
    fun prepare(vertx: io.vertx.core.Vertx, testContext: VertxTestContext) {
        dbConfig = InitPg.startPg()
        testContext.completeNow()
    }

    @DisplayName( "Setup data" )
    @BeforeEach
    fun prepareEach( vertx: io.vertx.core.Vertx, testContext: VertxTestContext ) {
        dbSetup( to = InitPg.dataSource!!)
        {
            deleteAllFrom( "cats" )
            insertInto( "cats" ) {
                columns( "name", "type" )
                values( "mitzy", "calico")
                values( "patches", "tabby" )
            }
        }.launchWith( dbSetupTracker )
        testContext.completeNow()
    }

    @DisplayName( "Get all cats" )
    @Test
    fun testAll() {
        dbSetupTracker.skipNextLaunch()
        val testContext = VertxTestContext()
        val vertx = Vertx.vertx()
        val client = PgClient.pool( vertx, PgPoolOptions( dbConfig ) )
        CatsDao().all( client.rxGetConnection() )
            .doAfterTerminate {
              client.close()
              testContext.completeNow()
            }
                .subscribe { jsonArray ->
                    assertThat( jsonArray.size() ).isEqualTo( 2 )
                    assertThat( jsonArray )
                            .contains( json{ obj( "name" to "mitzy",
                                                  "type" to "calico" ) },
                                       json{ obj( "name" to "patches",
                                                  "type" to "calico") } )
                }

    }
}
