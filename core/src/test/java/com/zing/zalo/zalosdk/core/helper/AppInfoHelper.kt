package com.zing.zalo.zalosdk.core.helper

object AppInfoHelper {
    const val appId = "123456"
    const val scanId = "3"
    const val appName = "ABC"
    const val versionName = "2"
    const val applicationHashKey = "applicationHashKey"

    fun setup() {
        AppInfo.extracted = true
        AppInfo.appId = appId
        AppInfo.appName = appName
        AppInfo.versionName = versionName
        AppInfo.applicationHashKey = applicationHashKey
    }
}