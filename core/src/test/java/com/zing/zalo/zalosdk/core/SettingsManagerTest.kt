package com.zing.zalo.zalosdk.core

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.zing.zalo.zalosdk.core.helper.AppInfoHelper
import com.zing.zalo.zalosdk.core.helper.PrivateSharedPreferenceInterface
import com.zing.zalo.zalosdk.core.http.HttpClient
import com.zing.zalo.zalosdk.core.http.HttpGetRequest
import com.zing.zalo.zalosdk.core.http.HttpResponse
import com.zing.zalo.zalosdk.core.settingsmanager.GetSDKSettingAsyncTask
import com.zing.zalo.zalosdk.core.settingsmanager.SettingsManager
import com.zing.zalo.zalosdk.core.settingsmanager.SettingsManager.Companion.KEY_EXPIRE_TIME
import com.zing.zalo.zalosdk.core.settingsmanager.SettingsManager.Companion.KEY_SETTINGS_OUT_APP_LOGIN
import com.zing.zalo.zalosdk.core.settingsmanager.SettingsManager.Companion.KEY_SETTINGS_WEB_VIEW
import com.zing.zalo.zalosdk.core.settingsmanager.SettingsManager.Companion.KEY_WAKEUP_ENABLE
import com.zing.zalo.zalosdk.core.settingsmanager.SettingsManager.Companion.KEY_WAKEUP_INTERVAL
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyLong
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsManagerTest {
    private lateinit var context: Context

    @MockK
    private lateinit var client: HttpClient

    @MockK
    private lateinit var response: HttpResponse

    @MockK
    private lateinit var storage: PrivateSharedPreferenceInterface

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        context = ApplicationProvider.getApplicationContext()
        AppInfoHelper.setup()
    }

    @Test
    fun `cached settings`() {
        val sut = SettingsManager(context)

        //1. mock
        sut.wakeUpStorage = storage
        sut.httpClient = client

        every { storage.getLong(KEY_EXPIRE_TIME) } returns System.currentTimeMillis() + 1000
        every { storage.getBoolean(KEY_SETTINGS_OUT_APP_LOGIN) } returns true
        every { storage.getBoolean(KEY_SETTINGS_WEB_VIEW) } returns true
        every { storage.getBoolean(KEY_WAKEUP_ENABLE) } returns true
        every { storage.getLong(KEY_WAKEUP_INTERVAL) } returns 3600

        //2. run
        sut.init()

        //3. verify
        verify(exactly = 0) { client.send(any()) }
        assertThat(sut.isLoginViaBrowser()).isTrue()
        assertThat(sut.isUseWebViewLoginZalo()).isTrue()
        assertThat(sut.getWakeUpSetting()).isTrue()
        assertThat(sut.getWakeUpInterval()).isEqualTo(3600)
    }

    @Test
    fun `load settings success`() {
        val sut = SettingsManager(context)

        //1. mock
        val data = """{
            "data":{
                "webview_login":1,
                "isOutAppLogin":true,
                "setting":{
                    "wakeup_interval_enable":true,
                    "wakeup_send_gid_to_other_app_enable":true,
                    "wakeup_interval":86400000,
                    "expiredTime":86400000
                }
            },
            "error":0,"errorMsg":"Success."
        }"""
        sut.wakeUpStorage = storage
        sut.httpClient = client

        val request = slot<HttpGetRequest>()
        val expireTime = slot<Long>()
        val now = System.currentTimeMillis()
        every { response.getJSON() } returns JSONObject(data)
        every { client.send(capture(request)) } returns response
        every { storage.getLong(KEY_EXPIRE_TIME) } returns now - 1000
        every { storage.setLong(KEY_EXPIRE_TIME, capture(expireTime)) } just Runs

        //2. run
        sut.init()
        TestUtils.waitTaskRunInBackgroundAndForeground()

        //3. verify
        verify(exactly = 1) { client.send(any()) }
        verify(exactly = 1) { storage.setBoolean(KEY_SETTINGS_OUT_APP_LOGIN, true) }
        verify(exactly = 1) { storage.setBoolean(KEY_SETTINGS_WEB_VIEW, true) }
        verify(exactly = 1) { storage.setBoolean(KEY_WAKEUP_ENABLE, true) }
        verify(exactly = 1) { storage.setLong(KEY_WAKEUP_INTERVAL, 86400000L) }
        assertThat(expireTime.captured).isGreaterThan(now + 86400000 - 1000)

        assertThat(request.captured.getUrl("")).isEqualTo(
            "/sdk/mobile/setting?pl=android&appId=${AppInfoHelper.appId}&sdkv=${Constant.VERSION}" +
                    "&pkg=${context.packageName}&zdId=${AppInfoHelper.zdId}"
        )
    }
}