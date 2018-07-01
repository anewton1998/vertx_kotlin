// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import javax.sql.DataSource

object InitPg {
    var pgStarted = false
    var pg : EmbeddedPostgres? = null
    var dataSource : DataSource? = null
    var dbConfig : JsonObject = JsonObject()

    var batch = mutableListOf<String>()

    init {
        batch.add( """
            DROP SCHEMA public CASCADE
        """)
        batch.add( """
            CREATE SCHEMA public
        """)

        // Person
        batch.add( """
            create table person (
              name varchar(255) primary key,
              password varchar(255)
            )
            """ )
        batch.add( """
            insert into person values
              ( 'bob', '12345' ),
              ( 'alice', 'abcde' ),
              ( 'julien', 'xyz' )
            """)

        // Device
        batch.add( """
            create sequence device_id_seq
        """.trimIndent())
        batch.add( """
            create table device (
                owner varchar(255),
                id integer,
                model varchar(255),
                primary key( id ),
                foreign key( owner ) references person(name)
            )
        """.trimIndent())
        batch.add( """
            insert into device values
              ( 'bob', nextval('device_id_seq'), 'samsung' ),
              ( 'bob', nextval('device_id_seq'), 'iphone' ),
              ( 'bob', nextval('device_id_seq'), 'wince' ),
              ( 'alice', nextval('device_id_seq'), 'wince' ),
              ( 'alice', nextval('device_id_seq'), 'nexus' ),
              ( 'alice', nextval('device_id_seq'), 'pixel' )
        """.trimIndent())

        // Cat
        batch.add( """
            create table cat (
                name varchar(255) primary key,
                type varchar(50)
            )
        """.trimIndent())
    }

    fun startPg() : JsonObject {
        if( !pgStarted ) {
            pg = EmbeddedPostgres.start()
            println( "Embedded Postgres started on port ${pg!!.getPort()}" )
            pgStarted = true
            dbConfig = json{
                obj(
                        "port" to pg!!.getPort(),
                        "host" to "localhost",
                        "database" to "postgres",
                        "user" to "postgres",
                        "password" to "secret",
                        "maxSize" to 5
                )
            }
        }
        pg?.let{
            dataSource = pg!!.postgresDatabase
            val connection = pg!!.getPostgresDatabase().getConnection()
            connection.autoCommit = false
            val stmt = connection.createStatement()
            batch.forEach{
                stmt.addBatch( it )
            }
            stmt.executeBatch()
            connection.commit()
        }
        return dbConfig
    }
}
