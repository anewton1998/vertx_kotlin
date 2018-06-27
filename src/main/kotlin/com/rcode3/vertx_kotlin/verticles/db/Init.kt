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