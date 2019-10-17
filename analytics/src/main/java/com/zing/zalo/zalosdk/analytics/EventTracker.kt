package com.zing.zalo.zalosdk.analytics

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.zing.zalo.devicetrackingsdk.DeviceTracking
import com.zing.zalo.devicetrackingsdk.DeviceTrackingListener
import com.zing.zalo.zalosdk.analytics.model.Event
import com.zing.zalo.zalosdk.core.helper.AppInfo
import com.zing.zalo.zalosdk.core.helper.DeviceInfo
import com.zing.zalo.zalosdk.core.helper.Storage
import com.zing.zalo.zalosdk.core.helper.Utils
import com.zing.zalo.zalosdk.core.http.HttpClient
import com.zing.zalo.zalosdk.core.http.HttpUrlEncodedRequest
import com.zing.zalo.zalosdk.core.log.Log
import com.zing.zalo.zalosdk.core.servicemap.ServiceMapManager
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class EventTracker(var context: Context) : IEventTracker {

    //TODO: check class này có thread safe hay ko?


    companion object {
        const val ACT_DISPATCH_EVENTS = 0x5000
        const val ACT_DISPATCH_EVENT_IMMEDIATE = 0x5001
        const val ACT_PUSH_EVENTS = 0x5002
//        const val ACT_STORE_EVENTS = 0x5003
//        const val ACT_LOAD_EVENTS = 0x5004

        const val DELAY_SECOND = 120
        var thread = HandlerThread("zdt-event-tracker", HandlerThread.MIN_PRIORITY)

        init {
            thread.start()
        }
    }

    var eventStorage = EventStorage(context)

    var handler: Handler
    private var listener: EventTrackerListener? = null

    lateinit var dispatchHandler: Handler
    private var dispatchRunnable = object : Runnable {
        override fun run() {
            dispatchEvent()
            dispatchHandler.postDelayed(this, DELAY_SECOND * 1000L)
        }
    }

    internal var httpClient = HttpClient(
        ServiceMapManager.urlFor(
            ServiceMapManager.KEY_URL_CENTRALIZED
        )
    )
    internal var request = HttpUrlEncodedRequest(Constant.core.api.API_TRACKING_URL)

    init {
        Log.d("EventTracker", "start thread zdt-event-tracker")
        handler = Handler(thread.looper, Handler.Callback {
            this.handleMessage(it)
        })

        dispatchHandler = Handler(thread.looper)

//        loadEvents()
    }

    //#region handle send message for method
    override fun addEvent(action: String, params: Map<String, String>, timestamp: Long) {
        /** @see handleMessage */
        Log.d("handleMessage", "ACT_PUSH_EVENTS_FUNCTION")
        val event = Event(action, params, timestamp)
        val msg = Message()
        msg.what = ACT_PUSH_EVENTS
        msg.obj = event
        handler.sendMessage(msg)
    }

    override fun addEvent(event:Event) {
        /** @see handleMessage */
        Log.d("handleMessage", "ACT_PUSH_EVENTS_FUNCTION")
        val msg = Message()
        msg.what = ACT_PUSH_EVENTS
        msg.obj = event
        handler.sendMessage(msg)
    }

    override fun dispatchEvent() {
        /** @see handleMessage */
        Log.d("handleMessage", "ACT_DISPATCH_EVENTS_FUNCTION")
        val msg = Message()
        msg.what = ACT_DISPATCH_EVENTS
        handler.sendMessage(msg)
    }

    //TODO: [done] save, dispatch xong, thành công -> xoá
    override fun dispatchEventImmediate(event: Event?) {
        /** @see handleMessage */
        if (event == null) return

        val msg = Message()
        msg.what = ACT_DISPATCH_EVENT_IMMEDIATE
        msg.obj = event
        handler.sendMessage(msg)
    }

//    fun loadEvents() {
//        /** @see handleMessage */
//        val msg = Message()
//        msg.what = ACT_LOAD_EVENTS
//        handler.sendMessage(msg)
//    }

//    fun storeEvents() {
//        /** @see handleMessage */
//        val msg = Message()
//        msg.what = ACT_STORE_EVENTS
//        handler.sendMessage(msg)
//    }

    //#endregion

    fun setListener(listener: EventTrackerListener) {
        this.listener = listener
    }

    fun runDispatchEventLoop() {
        dispatchHandler.post(dispatchRunnable)
    }

    //#region private supportive method
    private fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            ACT_DISPATCH_EVENTS -> {
                Log.d("handleMessage", "ACT_DISPATCH_EVENTS")
                DeviceTracking.getDeviceId(object : DeviceTrackingListener {
                    override fun onComplete(result: String?) {
                        val events = eventStorage.loadEventsFromDevice()
                        doDispatchEvent(events)
                    }
                })
            }
            ACT_DISPATCH_EVENT_IMMEDIATE -> {
                Log.d("handleMessage", "ACT_DISPATCH_EVENT_IMMEDIATE")
                DeviceTracking.getDeviceId(object : DeviceTrackingListener {
                    override fun onComplete(result: String?) {
                        val e = mutableListOf<Event>()
                        e.add(msg.obj as Event)
                        eventStorage.addEvent(msg.obj as Event)
                        doDispatchEvent(e)
                    }
                })
            }
            ACT_PUSH_EVENTS -> {
                Log.d("handleMessage", "ACT_PUSH_EVENTS")
                eventStorage.addEvent(msg.obj as Event)
            }
//            ACT_STORE_EVENTS -> {
//                Log.d("handleMessage", "ACT_STORE_EVENTS")
//                eventStorage.storeEventsToDevice()
//            }
//            ACT_LOAD_EVENTS -> {
//                Log.d("handleMessage", "ACT_LOAD_EVENTS")
//                eventStorage.loadEventsFromDevice()
//            }
            else -> return false
        }
        return true
    }

    private fun doDispatchEvent(events: List<Event>) {
        val storage = Storage(context)

        try {
            if (events.isEmpty())
                return

            val appData = JSONArray()
            val eventData = prepareEventData(events)
            val zdId = DeviceTracking.getDeviceId() ?: ""

            val an = AppInfo.getAppName(context)
            val av = AppInfo.getVersionName(context)
            val appId = AppInfo.getAppId(context)
            val oauthCode = storage.getOAuthCode() ?: ""
            val ts = Date().time.toString()
            val strEventData = eventData.toString()
            val strAppData = appData.toString()
            val strSocialAcc = "[]"
            val packageName = context.packageName
            val params = arrayOf(
                "pl",
                "appId",
                "oauthCode",
                "data",
                "apps",
                "ts",
                "zdId",
                "an",
                "av",
                "et",
                "gzip",
                "socialAcc",
                "packageName"
            )
            val values = arrayOf(
                "android",
                appId,
                oauthCode,
                strEventData,
                strAppData,
                ts,
                zdId,
                an,
                av,
                "0",
                "0",
                strSocialAcc,
                packageName
            )

            val sig = Utils.getSignature(
                params,
                values,
                Constant.core.key.TRK_SECRET_KEY
            )
            request.addParameter("pl", "android")
            request.addParameter("appId", appId)
            request.addParameter("oauthCode", oauthCode)
            request.addParameter("zdId", zdId)
            request.addParameter("data", strEventData)
            request.addParameter("apps", strAppData)
            request.addParameter("ts", ts)
            request.addParameter("sig", sig)
            request.addParameter("an", an)
            request.addParameter("av", av)
            request.addParameter("gzip", "0")
            request.addParameter("et", "0")
            request.addParameter("socialAcc", strSocialAcc)
            request.addParameter("packageName", context.packageName)

            val jsonObject = httpClient.send(request).getJSON() ?: return

            val errorCode = jsonObject.getInt("error")
            if (errorCode != 0) return

            Log.d("doDispatchEvent", "success dispatch to server ")
            eventStorage.clearEventStorage()
            listener?.dispatchComplete()
        } catch (e: Exception) {
            Log.e("doDispatchEvent", e)
            eventStorage.storeEventsToDevice()
            listener?.dispatchComplete()
        }

    }

    @Throws(Exception::class)
    private fun prepareEventData(events: List<Event>): JSONObject {
        val data = JSONObject()
        val deviceInfoData = DeviceInfo.prepareTrackingData(context, DeviceTracking.getDeviceId()?: "", System.currentTimeMillis())

        val jsonEvents = JSONArray()
        var jsonEvent: JSONObject

        for (e in events) {
            jsonEvent = JSONObject()
            val extras = e.params
            if (extras.containsKey("name")) {
                jsonEvent.put("name", extras["name"])
            }
            jsonEvent.put("extras", extras)
            jsonEvent.put("act", e.action)
            jsonEvent.put("ts", e.timestamp)

            jsonEvents.put(jsonEvent)
        }
        data.put("evt", jsonEvents)
        data.put("dat", deviceInfoData)


        return data
    }

    //#endregion

}