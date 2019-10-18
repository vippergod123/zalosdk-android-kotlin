package com.zing.zalo.provider

import android.content.Context
import androidx.annotation.NonNull
import com.zing.zalo.devicetrackingsdk.DeviceTracking
import com.zing.zalo.devicetrackingsdk.DeviceTrackingListener
import com.zing.zalo.devicetrackingsdk.SdkTracking
import com.zing.zalo.zalosdk.core.settings.SettingsManager

object ZaloBaseApp {

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
                SettingsManager(applicationContext).init()
            }
        })

        runDispatchEvent(context)
    }

    //    val eventTracker = EventTracker(this)
//    eventTracker.runDispatchEventLoop()
    private fun runDispatchEvent(ctx: Context) {
        try {
            val eventClass = Class.forName("com.zing.zalo.zalosdk.analytics.EventTracker")
//            val method = eventClass.getMethod("",null)
//
//            eventClass.getConstructor(Context::class.java)
//            eventClass.newInstance(s)
//            method.invoke(null, null)
//            val constructor = eventClass.getConstructor(Context::class.java)
//            val myObj = constructor.newInstance(ctx)
//            val myObjMethod = myObj.javaClass.getMethod("runDispatchEventLoop",null)
//            myObjMethod.invoke(myObj,null)
//             Log.d("")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}