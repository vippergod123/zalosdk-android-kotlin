package com.zing.zalo.zalosdk.openapi

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.zing.zalo.zalosdk.core.http.HttpClient
import com.zing.zalo.zalosdk.core.http.HttpUrlEncodedRequest
import com.zing.zalo.zalosdk.core.module.ModuleManager
import com.zing.zalo.zalosdk.oauth.ZaloSDK
import com.zing.zalo.zalosdk.oauth.helper.AuthStorage
import com.zing.zalo.zalosdk.openapi.helper.AppInfoHelper
import com.zing.zalo.zalosdk.openapi.helper.DataHelper
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.ref.WeakReference

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ZaloOpenApiTest {

    private lateinit var context: Context

    @MockK
    private lateinit var request: HttpUrlEncodedRequest
    @MockK
    private lateinit var httpClient: HttpClient

    private lateinit var authStorage: AuthStorage

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        context = ApplicationProvider.getApplicationContext()
        authStorage = AuthStorage(context)
        authStorage.setAuthCode("auth_code_abc")
        AppInfoHelper.setup()
        ModuleManager.initializeApp(context)
    }

    @Test
    fun `Get Access Token`() {
        every { httpClient.send(request).getJSON() } returns JSONObject(DataHelper.accessTokenData)

        val sut = GetAccessTokenAsyncTask(WeakReference(context), object : ZaloOpenApiCallback {
            override fun onResult(data: JSONObject?) {
                val accessToken = data?.getString("access_token")
                assertThat(accessToken).isEqualTo(DataHelper.accessToken)
            }

        })
        sut.request = request
        sut.httpClient = httpClient
        sut.execute()
        verifyRequest(request, 1)
    }


    @Test
    fun `get Profile with access token valid`() {
        val mock = spyk<ZaloOpenApi>(recordPrivateCalls = true)
        every { mock["isAccessTokenValid"]() } returns true
        every { mock getProperty "enableUnitTest" }  returns true

        every { httpClient.send(any()).getJSON() } returns JSONObject(DataHelper.profile)
        val fields = arrayOf("id", "birthday", "gender", "picture", "name")

        val callback = object : ZaloOpenApiCallback {
            override fun onResult(data: JSONObject?) {
                assertThat(data.toString()).isEqualTo(DataHelper.profile)
            }
        }

        val callApiAsyncTask = CallApiAsyncTask(callback)
        callApiAsyncTask.httpClient = httpClient
        mock.callApiAsyncTask = callApiAsyncTask
        mock.getProfile(fields, callback)

        verifyRequest(request, 0)
    }

    @Test
    fun `get Profile when access token invalid `() {
        val mock = spyk<ZaloOpenApi>(recordPrivateCalls = true)
        every { mock["isAccessTokenValid"]() } returns false
        every { mock getProperty "enableUnitTest" }  returns true


        every {
            httpClient.send(any()).getJSON()
        } returns JSONObject(DataHelper.accessTokenData) andThen JSONObject(DataHelper.profile)
        val fields = arrayOf("id", "birthday", "gender", "picture", "name")

        val callback = object : ZaloOpenApiCallback {
            override fun onResult(data: JSONObject?) {
                assertThat(data.toString()).isEqualTo(DataHelper.profile)
            }
        }

        val getAccessTokenAsyncTask = GetAccessTokenAsyncTask(WeakReference(context), null)
        getAccessTokenAsyncTask.request = request
        getAccessTokenAsyncTask.httpClient = httpClient
        val callApiAsyncTask = CallApiAsyncTask(callback)
        callApiAsyncTask.httpClient = httpClient
        mock.callApiAsyncTask = callApiAsyncTask
        mock.getAccessTokenAsyncTask = getAccessTokenAsyncTask
        mock.getProfile(fields, callback)


        verifyRequest(request, 1)
    }

    @Test
    fun `get Profile when auth code invalid `() {
        val mock = spyk<ZaloOpenApi>(recordPrivateCalls = true)
        every { mock["isAccessTokenValid"]() } returns false
        every { mock getProperty "enableUnitTest" }  returns true


        every {
            httpClient.send(any()).getJSON()
        } returns JSONObject(DataHelper.accessTokenData) andThen JSONObject(DataHelper.profile)
        val fields = arrayOf("id", "birthday", "gender", "picture", "name")

        val callback = object : ZaloOpenApiCallback {
            override fun onResult(data: JSONObject?) {
                val invalidAuthCodeResult = "{\"error\":-1019}"
                assertThat(data.toString()).isEqualTo(invalidAuthCodeResult)
            }
        }

        val getAccessTokenAsyncTask = GetAccessTokenAsyncTask(WeakReference(context), null)
        authStorage.setAuthCode("")
        getAccessTokenAsyncTask.request = request
        getAccessTokenAsyncTask.httpClient = httpClient
        val callApiAsyncTask = CallApiAsyncTask(callback)
        callApiAsyncTask.httpClient = httpClient
        mock.callApiAsyncTask = callApiAsyncTask
        mock.getAccessTokenAsyncTask = getAccessTokenAsyncTask
        mock.getProfile(fields, callback)


        verifyRequest(request, 0)
    }



    private fun verifyRequest(request: HttpUrlEncodedRequest, times: Int) {
        verify(exactly = times) { request.addQueryStringParameter("code", any()) }
        verify(exactly = times) {
            request.addQueryStringParameter(
                "pkg_name",
                AppInfoHelper.packageName
            )
        }
        verify(exactly = times) {
            request.addQueryStringParameter(
                "sign_key",
                AppInfoHelper.applicationHashKey
            )
        }
        verify(exactly = times) { request.addQueryStringParameter("app_id", AppInfoHelper.appId) }
        verify(exactly = times) { request.addQueryStringParameter("version", any()) }
        verify(exactly = times) { request.addQueryStringParameter("zdevice", any()) }
        verify(exactly = times) { request.addQueryStringParameter("ztracking", any()) }
    }
}
