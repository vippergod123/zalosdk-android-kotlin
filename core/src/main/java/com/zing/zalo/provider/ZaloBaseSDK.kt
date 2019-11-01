package com.zing.zalo.provider

import android.content.Context
import androidx.annotation.NonNull
import com.zing.zalo.devicetrackingsdk.DeviceTracking
import com.zing.zalo.devicetrackingsdk.DeviceTrackingListener
import com.zing.zalo.devicetrackingsdk.SdkTracking
import com.zing.zalo.zalosdk.core.log.Log
import com.zing.zalo.zalosdk.core.settings.SettingsManager

object ZaloBaseSDK {

    private var isInitialized = false


    fun initializeApp(@NonNull context: Context?) {
        if (isInitialized) return

        val applicationContext =
            if (context?.applicationContext == null) return
            else context.applicationContext

        isInitialized = true
        DeviceTracking.sdkTracking = SdkTracking(applicationContext)
        DeviceTracking.init(applicationContext, object : DeviceTrackingListener {
            override fun onComplete(result: String?) {
                initZaloSDKLib(applicationContext)
            }
        })

    }

    private fun initZaloSDKLib(ctx: Context) {
        SettingsManager(ctx).init()
        runDispatchEvent(ctx)
        zaloAuthInit(ctx)
        openApiInit(ctx)
    }

    private fun runDispatchEvent(ctx: Context) {
        try {
            val eventClass = Class.forName("com.zing.zalo.zalosdk.analytics.EventTracker")
            val constructor = eventClass.getConstructor(Context::class.java)
            val eventTracker = constructor.newInstance(ctx)
            val runDispatchEventLoopEventTrackerMethod =
                eventTracker::class.java.getDeclaredMethod("runDispatchEventLoop")

            runDispatchEventLoopEventTrackerMethod.isAccessible = true // set Accessible private method
            runDispatchEventLoopEventTrackerMethod.invoke(eventTracker)
        } catch (e: Exception) {
            Log.e("ZaloBaseSDK - runDispatchEvent", e)
        }
    }

    private fun zaloAuthInit(ctx: Context) {
        try {
            val zaloSDKClass = Class.forName("com.zing.zalo.zalosdk.oauth.ZaloSDK").kotlin
            val zaloSDK = zaloSDKClass.objectInstance ?: zaloSDKClass.java.newInstance()
            val initZaloSDKMethod = zaloSDK::class.java.getDeclaredMethod("initialize", Context::class.java)
            initZaloSDKMethod.isAccessible = true // set Accessible private method
            initZaloSDKMethod.invoke(zaloSDK, ctx)
        } catch (e: Exception) {
            Log.e("zaloAuthInit", e)
        }
    }

    private fun openApiInit(ctx: Context) {
        try {
            val openApiClass = Class.forName("com.zing.zalo.zalosdk.openapi.ZaloOpenApi").kotlin
            val openApi = openApiClass.objectInstance ?: openApiClass.java.newInstance()
            val initOpenApiMethod = openApi::class.java.getDeclaredMethod("init", Context::class.java)
            initOpenApiMethod.isAccessible = true // set Accessible private method
            initOpenApiMethod.invoke(openApi, ctx)
        } catch (e: Exception) {
            Log.e("openApiInit", e)
        }
    }

}