package com.rcode3.vertx_kotlin

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName( "Embeddeded PostgreSQL")
object TestEmbeddedPg {

    @DisplayName( "Setting up PostgreSQL" )
    @Test
    @Disabled
    fun prepare() {
        val pg = EmbeddedPostgres.start()
        val ds = pg.getPostgresDatabase()
        println( "datasource = $ds" )
        val connection = ds.getConnection()
        val rs = connection.createStatement().executeQuery( "select 1" )
        assertThat( rs.next() ).isTrue()
        assertThat( rs.getInt( 1 ) ).isEqualTo( 1 )
        assertThat( rs.next() ).isFalse()
        rs.close()
        connection.createStatement().execute( "create table users ( name varchar(25) ) ")
        connection.close()
    }

}