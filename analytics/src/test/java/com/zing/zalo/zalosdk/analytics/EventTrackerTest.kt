package com.zing.zalo.zalosdk.analytics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.zing.zalo.zalosdk.analytics.helper.DataHelper
import com.zing.zalo.zalosdk.analytics.model.Event
import com.zing.zalo.zalosdk.core.helper.UtilsJSON
import com.zing.zalo.zalosdk.core.log.Log
import io.mockk.MockKAnnotations
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EventTrackerTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun eventTrackerTest() {


        val data = JSONObject(DataHelper.EVENT_STORED_IN_DEVICE)

        val jsonArray = data.optJSONArray("events")
        val events = UtilsJSON.jsonArrayToArrayList<Event>(jsonArray)

        for (each in events)  {
            Log.d(each.params.toString())
        }
    }
}