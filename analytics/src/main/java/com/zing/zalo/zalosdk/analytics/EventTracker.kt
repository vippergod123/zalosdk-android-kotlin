package com.zing.zalo.zalosdk.analytics

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.zing.zalo.devicetrackingsdk.DeviceTracking
import com.zing.zalo.devicetrackingsdk.DeviceTrackingListener
import com.zing.zalo.zalosdk.analytics.model.Event
import com.zing.zalo.zalosdk.core.log.Log

class EventTracker(var context: Context) : IEventTracker {


    companion object {
        const val ACT_DISPATCH_EVENTS = 0x5000
        const val ACT_PUSH_EVENTS = 0x5001
        const val ACT_STORE_EVENTS = 0x5002
        const val ACT_LOAD_EVENTS = 0x5004

    }


    var tempMaxEventStored = Constant.DEFAULT_MAX_EVENTS_STORED
    var tempDipatchEventsInterval = Constant.DEFAULT_DISPATCH_EVENTS_INTERVAL
    var tempStoreEventsInterval = Constant.DEFAULT_STORE_EVENTS_INTERVAL

    private var thread = HandlerThread("zdt-event-tracker", HandlerThread.MIN_PRIORITY)
    private var handler: Handler

    private var eventStorage = EventStorage(context)

    init {
        thread.start()
        handler = Handler(thread.looper, Handler.Callback {
            this.handleMessage(it)
        })

        val msg = Message()
        msg.what = ACT_LOAD_EVENTS
        handler.sendMessage(msg)

        Log.d("Event Tracker", "start thread zdt-event-tracker")
    }

    override fun addEvent(action: String, params: Map<String, String>) {
        val event = Event(action, params)
        val msg = Message()
        msg.what = ACT_PUSH_EVENTS
        msg.obj = event
        handler.sendMessage(msg)
    }

    override fun dispatchEvent() {
        try {
            run {
                val msg = Message()
                msg.what = ACT_DISPATCH_EVENTS
                handler.sendMessage(msg)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    //#region private supportive method
    private fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            ACT_DISPATCH_EVENTS ->
                DeviceTracking.getDeviceId(object : DeviceTrackingListener {
                    override fun onComplete(result: String?) {
                        Log.d("handleMessage", result.toString())
                    }
                })


            ACT_STORE_EVENTS -> {
                Log.d("handleMessage", "ACT_STORE_EVENTS")
                eventStorage.storeEventToDevice()
            }

            ACT_PUSH_EVENTS -> {
                Log.d("handleMessage", "ACT_PUSH_EVENTS")
                eventStorage.addEvent(msg.obj as Event)
            }

            ACT_LOAD_EVENTS -> Log.d("handleMessage", "ACT_LOAD_EVENTS")
            else -> return false
        }
        return true
    }
    //#endregion

}