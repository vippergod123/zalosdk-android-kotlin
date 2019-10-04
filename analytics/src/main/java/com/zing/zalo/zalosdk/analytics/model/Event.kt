package com.zing.zalo.zalosdk.analytics.model

import org.json.JSONObject

data class Event (var action:String, var params: Map<String, String>){
}