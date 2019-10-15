package com.zing.zalo.zalosdk.analytics

import com.zing.zalo.zalosdk.core.Constant


object Constant {
    val core = Constant
    val eventDatabase = EventDatabase

    const val DEFAULT_MAX_EVENTS_STORED = 100
    const val DEFAULT_DISPATCH_EVENTS_INTERVAL = 120 * 1000L
    const val DEFAULT_STORE_EVENTS_INTERVAL = 60 * 1000L

    const val MIN_DISPATCH_EVENTS_INTERVAL = 10 * 1000L
    const val MIN_STORE_EVENTS_INTERVAL = 10 * 1000L
    const val DEFAULT_DISPATCH_MAX_COUNT_EVENT = 100
    const val DEFAULT_VALID_EVENTS = 2 * 24 * 60 * 1000L
    const val DEFAULT_DISPATCH_EVENTS_IN_APP_DURATION_INTERVAL = 1 * 24 * 60 * 60 * 1000L
    const val DEFAULT_CHECK_DISPATCH_EVENTS_IN_APP_DURATION_INTERVAL = 60 * 60 * 1000L
}



object EventDatabase {

    const val NAME = "session.db"
    const val VERSION = 1
    const val TABLE_EVENT = "events"
    const val COLUMN_TIME = "time"
    const val COLUMN_ACTION = "action"
    const val COLUMN_DATA = "data"
}