package com.zing.zalo.zalosdk.analytics

import android.content.Context
import android.os.HandlerThread
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.zing.zalo.devicetrackingsdk.DeviceTracking
import com.zing.zalo.zalosdk.analytics.helper.AppInfoHelper
import com.zing.zalo.zalosdk.analytics.helper.DataHelper
import com.zing.zalo.zalosdk.analytics.helper.DeviceHelper
import com.zing.zalo.zalosdk.core.helper.AppInfo
import com.zing.zalo.zalosdk.core.helper.DeviceInfo
import com.zing.zalo.zalosdk.core.helper.Utils
import com.zing.zalo.zalosdk.core.http.HttpClient
import com.zing.zalo.zalosdk.core.http.HttpUrlEncodedRequest
import com.zing.zalo.zalosdk.core.log.Log
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.verify
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLooper


@RunWith(RobolectricTestRunner::class)
class EventTrackerTest {
    private lateinit var context: Context

    @MockK
    private lateinit var httpClient: HttpClient
    @MockK
    private lateinit var request: HttpUrlEncodedRequest

    private lateinit var eventTracker: EventTracker

    var testThread = HandlerThread("event-tracker-test", HandlerThread.MIN_PRIORITY)


    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        context = ApplicationProvider.getApplicationContext()

        EventTracker.thread = testThread
        EventTracker.thread.start()
        eventTracker = EventTracker(context)
    }

    @Test
    fun `dispatch Event To Server`() {
        mockDataWithDeviceIdNotExpired()
        DeviceTracking.init(context, null)
        //#1. Setup mock
        val event = DataHelper.mockEvent()
        val okResult = "{\"error\":0,\"errorMsg\":\"Success\"}"


        eventTracker.setListener(object : EventTrackerListener {
            override fun dispatchSuccess() {
                super.dispatchSuccess()
                Log.d("got here")
                assertThat(EventStorage.events).isEmpty()
            }
        })


        every { httpClient.send(any()).getJSON() } returns JSONObject(okResult)
        eventTracker.httpClient = httpClient
        eventTracker.request = request
        //#2. Execute
        eventTracker.loadEvents()
        eventTracker.addEvent(event.action, event.params, event.timestamp)
        eventTracker.storeEvents()
        eventTracker.dispatchEvent()


        //wait for complete thread's task
        shadowOf(EventTracker.thread.looper).idle()
        //#3. verify
        verifyRequest(request, 1)
    }


    @Test
    fun `dispatch Immediately Event To Server`() {
        mockDataWithDeviceIdNotExpired()
        DeviceTracking.init(context, null)

        //#1. Setup mock
        val event = DataHelper.mockEvent()
        val okResult = "{\"error\":0,\"errorMsg\":\"Success\"}"


        eventTracker.setListener(object : EventTrackerListener {
            override fun dispatchSuccess() {
                super.dispatchSuccess()
                Log.d("got here")
                assertThat(EventStorage.events).isEmpty()
            }
        })


        every { httpClient.send(any()).getJSON() } returns JSONObject(okResult)
        eventTracker.httpClient = httpClient
        eventTracker.request = request
        //#2. Execute
        eventTracker.dispatchEventImmediate(event)
        //wait for complete thread's task
        shadowOf(EventTracker.thread.looper).idle()

        //#3. verify
        verifyRequest(request, 1)
    }



    //#region private supportive method

    private fun mockDataWithDeviceIdExpired() {

        val expiredTime = System.currentTimeMillis() + 10000L
        mockData(expiredTime)
    }

    private fun mockDataWithDeviceIdNotExpired() {
        mockData(0L)
    }

    private fun mockData(deviceExpiredTime: Long) {
        mockkObject(DeviceInfo)
        mockkObject(DeviceTracking)
        mockkObject(Utils)
        mockkObject(AppInfo)

        val deviceIdSettingJSON =
            "{\"deviceId\":\"${DeviceHelper.deviceId}\",\"expiredTime\":\"${deviceExpiredTime}\"}"
//        every { Utils.readFromFile(context, EventStorage.EVENTS_FILE_NAME) } returns DataHelper.EVENT_STORED_IN_DEVICE
        every {
            Utils.readFromFile(
                context,
                DeviceTracking.DID_FILE_NAME
            )
        } returns deviceIdSettingJSON

        every { DeviceInfo.getAdvertiseID(context) } returns DeviceHelper.adsId
        every { AppInfo.getVersionName(context) } returns AppInfoHelper.versionName
        every { AppInfo.getAppName(context) } returns AppInfoHelper.appName
        every { AppInfo.getAppId(context) } returns AppInfoHelper.appId

        every { DeviceTracking.getDeviceId() } returns DeviceHelper.deviceId
    }

    private fun verifyRequest(request: HttpUrlEncodedRequest, times: Int) {
        verify(exactly = times) { request.addParameter("pl", "android") }
        verify(exactly = times) { request.addParameter("appId", AppInfoHelper.appId) }
        verify(exactly = times) { request.addParameter("oauthCode", any()) }
        verify(exactly = times) { request.addParameter("zdId", any()) }
        verify(exactly = times) { request.addParameter("data", any()) }
        verify(exactly = times) { request.addParameter("apps", any()) }
        verify(exactly = times) { request.addParameter("ts", any()) }
        verify(exactly = times) { request.addParameter("sig", any()) }
        verify(exactly = times) { request.addParameter("an", AppInfoHelper.appName) }
        verify(exactly = times) { request.addParameter("av", AppInfoHelper.versionName) }
        verify(exactly = times) { request.addParameter("gzip", any()) }
        verify(exactly = times) { request.addParameter("et", any()) }
        verify(exactly = times) { request.addParameter("socialAcc", any()) }
        verify(exactly = times) { request.addParameter("packageName", context.packageName) }

    }
    //#endregion
}