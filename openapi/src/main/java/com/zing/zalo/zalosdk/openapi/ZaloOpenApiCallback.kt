package com.zing.zalo.zalosdk.openapi

import org.json.JSONObject

interface ZaloOpenApiCallback {
    fun onResult(data: JSONObject?)
}