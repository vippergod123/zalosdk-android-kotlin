package com.zing.zalo.zalosdk.analytics

interface IEventTracker {
    fun addEvent(action: String, params: Map<String, String>)
    fun dispatchEvent()
}