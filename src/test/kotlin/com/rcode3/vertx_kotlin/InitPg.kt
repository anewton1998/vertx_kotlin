// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin

import com.opentable.db.postgres.embedded.EmbeddedPostgres

object InitPg {
    var pgStarted = false
    var pg : EmbeddedPostgres? = null
    var port = 0

    var batch = mutableListOf<String>()

    init {
        batch.add( """
            DROP SCHEMA public CASCADE
        """)
        batch.add( """
            CREATE SCHEMA public
        """)

        // Users
        batch.add( """
            create table users (
              name varchar(255) primary key,
              password varchar(255)
            )
            """ )
        batch.add( """
            insert into users values
              ( 'bob', '12345' ),
              ( 'alice', 'abcde' ),
              ( 'julien', 'xyz' )
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
                foreign key( owner ) references users(name)
            )
        """.trimIndent())
        batch.add( """
            insert into devices values
              ( 'bob', nextval('device_id_seq'), 'samsung' ),
              ( 'bob', nextval('device_id_seq'), 'iphone' ),
              ( 'bob', nextval('device_id_seq'), 'wince' ),
              ( 'alice', nextval('device_id_seq'), 'wince' ),
              ( 'alice', nextval('device_id_seq'), 'nexus' ),
              ( 'alice', nextval('device_id_seq'), 'pixel' )
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
    }

    fun startPg() : Int {
        if( !pgStarted ) {
            pg = EmbeddedPostgres.start()
            port = pg!!.getPort()
            println( "Embedded Postgres started on port ${port}" )
            pgStarted = true
        }
        pg?.let{
            val connection = pg!!.getPostgresDatabase().getConnection()
            connection.autoCommit = false
            val stmt = connection.createStatement()
            batch.forEach{
                stmt.addBatch( it )
            }
            stmt.executeBatch()
            connection.commit()
        }
        return port
    }
}
