package com.zing.zalo.zalosdk.analytics

import android.content.Context
import com.zing.zalo.zalosdk.analytics.model.Event
import com.zing.zalo.zalosdk.core.helper.Storage
import com.zing.zalo.zalosdk.core.helper.Utils
import com.zing.zalo.zalosdk.core.helper.UtilsJSON
import com.zing.zalo.zalosdk.core.log.Log
import org.json.JSONArray
import org.json.JSONObject

class EventStorage(context: Context) : Storage(context) {



    companion object {
        private const val EVENTS_FILE_NAME = "z-event-tracker"
        var events: MutableList<Event> = mutableListOf()
    }

    fun addEvent(e: Event) {
        events.add(e)
    }


    fun loadEvents() {
        val obj = Utils.readFromFile(context, EVENTS_FILE_NAME)
        Log.d("loadEvents",obj.toString())

    }

    fun storeEventToDevice() {
        val jsonArray = JSONArray()
        for (e in events) {
            val data = JSONObject()
            val params = UtilsJSON.mapToJSONObject(e.params)
            data.put("params", params)
            data.put("action", e.action)

            jsonArray.put(data)
        }
        val data = JSONObject()
        data.put("data", jsonArray)
        Utils.writeToFile(context, data.toString(), EVENTS_FILE_NAME)
    }
}