package com.zing.zalo.zalosdk.demo

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiObjectNotFoundException
import com.zing.zalo.zalosdk.analytics.EventStorage
import com.zing.zalo.zalosdk.analytics.EventTracker
import com.zing.zalo.zalosdk.core.helper.Utils
import com.zing.zalo.zalosdk.demo.helper.DataHelper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class EventTrackerTest : AppBase() {

    private lateinit var contextApp: Context
    private lateinit var eventStorage: EventStorage
    private lateinit var eventsTracker: EventTracker

    @Before
    @Throws(IOException::class, UiObjectNotFoundException::class)
    override fun startApp() {
        super.startApp()

        MockKAnnotations.init(this, relaxUnitFun = true)
        contextApp = context.applicationContext
        eventStorage = EventStorage(context)
        eventsTracker = EventTracker(context)
    }

    @Test
    @Throws(UiObjectNotFoundException::class, IOException::class)
    fun dispatchEventsToServer() {
        mockkObject(Utils)
        every { Utils.readFromFile(context, any()) } returns DataHelper.EVENT_STORED_IN_DEVICE

        eventStorage.loadEventsFromDevice()

        eventsTracker.dispatchEvent()
    }
}