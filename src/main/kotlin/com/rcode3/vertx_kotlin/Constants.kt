// Copyright (C) 2018 Andreew Newton
package com.rcode3.vertx_kotlin

//
//
// Event Bus Addresses that don't belong anywhere else
//
//

/**
 * Event bus address for a periodic timer
 */
const val PERIODIC_TIMER_ADDR = "example.timer"

//
//
// Common message failure codes
//
//

/**
 * The requested action is not implemented
 */
const val NOT_IMPLEMENTED = 503

//
//
// Common DAO Headers
//
//

/**
 * Header name for database actions. The value for the action should be one of:
 * - [ENTITY_COUNT]
 * - [ENTITY_GET]
 * - [ENTITY_SAVE]
 * - [ENTITY_DELETE]
 * - [ENTITY_ALL]
 */
const val DB_ACTION = "db.action"

/**
 * Header name for limiting the number of results when using [ENTITY_ALL]. The
 * value of the header should be an integer.
 */
const val QUERY_LIMIT = "query.limit"

/**
 * Header name for starting a query at an offset when useing [ENTITY_ALL]. The
 * value of the header should be an integer.
 */
const val QUERY_OFFSET = "query.offset"

/**
 * Get a count of the entities.
 */
const val ENTITY_COUNT = "entity.count"

/**
 * Get the entity by its primary key.
 */
const val ENTITY_GET = "entity.get"

/**
 * Save the entity.
 */
const val ENTITY_SAVE = "entity.save"

/**
 * Delete the entity by its primary key.
 */
const val ENTITY_DELETE = "entity.delete"

/**
 * Get all of the entities.
 */
const val ENTITY_ALL = "entity.all"

