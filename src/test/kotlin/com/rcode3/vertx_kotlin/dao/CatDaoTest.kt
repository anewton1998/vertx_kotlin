// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.dao

import com.ninja_squad.dbsetup.DbSetupTracker
import com.ninja_squad.dbsetup_kotlin.dbSetup
import com.ninja_squad.dbsetup_kotlin.launchWith
import com.rcode3.vertx_kotlin.DaoTestVerticle
import com.rcode3.vertx_kotlin.InitPg
import com.rcode3.vertx_kotlin.model.Cat
import io.reactiverse.reactivex.pgclient.PgPool
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.RxHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName( "CatDao DB Tests" )
@ExtendWith( VertxExtension::class )
object CatDaoTest {

    val dbSetupTracker = DbSetupTracker()
    var dbConfig = JsonObject()

    @DisplayName( "Prepare the database" )
    @BeforeAll
    @JvmStatic
    fun prepare( testContext: VertxTestContext) {
        dbConfig = InitPg.startPg()
        testContext.completeNow()
    }

    @DisplayName( "Setup data" )
    @BeforeEach
    fun prepareEach( testContext: VertxTestContext ) {
        dbSetup( to = InitPg.dataSource!!)
        {
            deleteAllFrom( CatDao.TABLE_NAME )
            insertInto( CatDao.TABLE_NAME ) {
                columns( CatDao.Column.NAME.cn, CatDao.Column.TYPE.cn )
                values( "mitzy", "calico")
                values( "patches", "tabby" )
            }
        }.launchWith( dbSetupTracker )
        testContext.completeNow()
    }

    @DisplayName( "Get all cats" )
    @Test
    fun testAll( vertx : Vertx, testContext: VertxTestContext ) {
        dbSetupTracker.skipNextLaunch()
        val test = { client : PgPool ->
            CatDao().all( client.rxGetConnection() )
                    .doFinally {
                        client.close()
                    }
                    .doOnError { throw it }
                    .map { cats ->
                        assertThat( cats.size ).isEqualTo( 2 )
                        assertThat( cats ).contains( Cat( name="mitzy", type="calico"),
                                Cat( name="patches", type="tabby" ) )
                        Unit
                    }
        }
        vertx.deployVerticle( DaoTestVerticle( dbConfig, test ), testContext.succeeding {
            testContext.completeNow()
        } )
    }

    @DisplayName( "Get some cats" )
    @Test
    fun testAllLimited( vertx: Vertx, testContext: VertxTestContext ) {
        dbSetupTracker.skipNextLaunch()
        val test = { client: PgPool ->
            CatDao().allLimited(client.rxGetConnection(), 1)
                    .doFinally {
                        client.close()
                    }
                    .doOnError { throw it }
                    .map { cats ->
                        assertThat(cats.size).isEqualTo(1)
                        Unit
                    }
        }
        vertx.deployVerticle( DaoTestVerticle( dbConfig, test ), testContext.succeeding {
            testContext.completeNow()
        } )
    }

    @DisplayName( "Count cats" )
    @Test
    fun testCount( vertx: Vertx, testContext: VertxTestContext ) {
        dbSetupTracker.skipNextLaunch()
        val test = { client: PgPool ->
            CatDao().count(client.rxGetConnection())
                    .doFinally {
                        client.close()
                    }
                    .doOnError { throw it }
                    .map { count ->
                        assertThat(count).isEqualTo(2)
                        Unit
                    }
        }
        vertx.deployVerticle( DaoTestVerticle( dbConfig, test ), testContext.succeeding {
            testContext.completeNow()
        } )
    }
}
