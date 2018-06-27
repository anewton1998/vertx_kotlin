// Copyright(C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.db

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.kotlin.core.json.get

class Init : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {

        val client = JDBCClient.createShared( vertx, config()[ "db" ] )

        var batch = mutableListOf<String>()

        batch.add( """
            DROP SCHEMA PUBLIC CASCADE
        """)

        // Users
        batch.add( """
            create table user (
              name varchar(255) primary key,
              password varchar(255)
            )
            """ )
        batch.add( """
            insert into user values
              ( 'bob', '12345' ),
              ( 'alice', 'abcde' )
            """)

        // Devices
        batch.add( """
            create sequence device_id_seq
        """.trimIndent())
        batch.add( """
            create table devices (
                owner varchar(255),
                id integer,
                model varchar(255),
                primary key( id ),
                foreign key( owner ) references user(name)
            )
        """.trimIndent())

        // Cats
        batch.add( """
            create table cats (
                name varchar(255) primary key,
                type varchar(50)
            )
        """.trimIndent())
        batch.add( """
            insert into cats values
                ( 'mitzy', 'calico' ),
                ( 'patches', 'tabby' )
        """.trimIndent())

        client.getConnection{
            if( it.failed() ) {
                startFuture.fail( it.cause() )
            }
            else
            {
                val connection = it.result()
                connection.batch( batch ) {
                    if( it.failed() ) {
                        startFuture.fail( it.cause() )
                    }
                    else {
                        startFuture.complete()
                    }
                }
            }
        }

    }


}