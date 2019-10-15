package com.zing.zalo.zalosdk.analytics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.zing.zalo.zalosdk.analytics.helper.DataHelper
import com.zing.zalo.zalosdk.analytics.sqlite.EventDataSource
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class EventDataSourceTest {

    private lateinit var context: Context
    private lateinit var eventDataSource: EventDataSource
    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        context = ApplicationProvider.getApplicationContext()
        eventDataSource = EventDataSource(RuntimeEnvironment.application)
    }

    @Test
    fun addEventToSql() {

        for (i in 0..150) {
            val e = DataHelper.mockEvent()
            eventDataSource.insertEvent(e)
            Thread.sleep(53)
        }
        val listEvents = eventDataSource.getListEvent()

        eventDataSource.deleteEvent(listEvents[0].timestamp)
    }
}