// Copyright (C) 2018 Andrew Newton
package com.rcode3.vertx_kotlin.verticles.example

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


@DisplayName( "Test of Vertx JSON" )
object JsonTest {

    data class Child( var firstName : String, var nickName : String )
    data class Parent( var firstName : String,
                       var lastName : String,
                       val children : List<Child> )
    val child1 = Child( "peter", "pete")
    val child2 = Child( "marsha", "cissy")
    val parent = Parent( "greg", "brady",
            listOf( child1, child2 ) )
    val json = "{\"firstName\":\"greg\",\"lastName\":\"brady\",\"children\":[{\"firstName\":\"peter\",\"nickName\":\"pete\"},{\"firstName\":\"marsha\",\"nickName\":\"cissy\"}]}"

    @Test
    fun testToJson() {
        val jsonString = Json.encode( parent )
        assertThat( jsonString ).isEqualTo( json )
    }

    @Test
    fun testFromJson() {
        Json.mapper.apply { registerKotlinModule() }
        val myParent = Json.decodeValue( json, Parent::class.java )
        assertThat( myParent ).isEqualTo( parent )
    }

}
