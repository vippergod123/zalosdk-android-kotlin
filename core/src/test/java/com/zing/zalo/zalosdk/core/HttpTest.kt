package com.zing.zalo.zalosdk.core

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.zing.zalo.zalosdk.core.http.HttpClient
import com.zing.zalo.zalosdk.core.http.HttpClientFactory
import com.zing.zalo.zalosdk.core.http.HttpMethod
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.HttpURLConnection

@RunWith(RobolectricTestRunner::class)
class HttpTest
{
     private lateinit var context: Context
     private var httpClientFactory = HttpClientFactory()
     private val SERVICE_MAP_URLS = arrayOf(
          "https://mp3.zing.vn/zdl/service_map_all.bin",
          "https://zaloapp.com/zdl/service_map_all.bin",
          "https://news.zing.vn/zdl/service_map_all.bin",
          "https://n.zing.vn/zdl/service_map_all.bin",
          "https://srv.mp3.zing.vn/zdl/service_map_all.bin"
     )
     private val resultServiceMap =
          "4CB543CAD8B1FBE8030AFE850F1F27FD01ACD1F05890C5E79C1D722C3E3AFCF20BB0C2D9804FF841AFBF481A6C3319B19D31C69E63D0FC734EBE1E94B63C4176BB7BBE1A74A480A5255FF8A59B9D623A18D757D0A2A8065A24E4D7E32EEDC77A40F93DFD803CFC52683CADBC95D72B303FEA503CF32CA1566132594F21D789B4FD026C0901E1E8566C084BEF5D14EE14A287BD42961FBA69D6AC5B8E020755BE5BF97FFE21D511D25033F51D01476ABFE040CCB724706417E2FFBF51A2DD6B030EA6E1C9BFF4326014ECC0F7208BB227318BFD6DE8EA6D3DA53A2B79BE668533C5AD05C5D978511B68B5ED6DF72BA20DEF638AA1B0C4ADB528DAC14DD11FC925CC16478339CCC2DAC63A6CC6C27B63DD40B24CA8B195B5143D7C3BE40083B712CBF5A31A8E3FDF1618D06160F57124DABDBBE82FAD860D9F8FE3B7CEA15A950026842C183F0D1CD47319AE7900817012347917BDF68ED69363B9C2252E20BCC705190981AC00078E561F77A7F7E28D62371AABE20DEEB807D7FF3BD175BFDBD398FB11D47C73520B6F9E03008CD5E874E1E5C71C5D3C55403655A3DC62C1C8B678FC74F94C5308A3C67F1DA35CDCBC976F65F5FE5E31D7FF87C631D794CBD60504EC65950602DAE1227EB024B182702754A0EDE2B65EE7BCC1464057FB7DE515FEF70EFC4BEC861AAE6E2E52358AA2FF675567ACB892F9C1550F80C7D84ACA0A870971930D22180BDCA3C4B69554CC6A8AE2A7E0C2786A4381C250232966F94F09A445F8CC4BBE43214143AF383B78A92B5E7CFB603398EC46C92C956874E2AAFB98799E054D5F84E75D6DFB280F94A37F68BC7F51BDADA690D59ED294164CC2AE88B36A725EF7AB3CBAE9B42B071AA534819BC080727943EC1B6FF39CD3F4BEFA48AE28B4AF694A54106E056D16EE8947EA9CA38813BFBA6C1EDEF3BB9E88A6CBA9EA79E2C711A290886FA13C3740EE"
     private val resultAuth =
          "{\"data\":{\"msg\":\"The code is still valid\",\"uid\":8278452768104753519,\"expires_in\":1566965510761},\"error\":0}"

     @Before
     fun setup()
     {
          MockKAnnotations.init(this, relaxUnitFun = true)
          context = ApplicationProvider.getApplicationContext()
     }

     @Test
     fun `create post http request`()
     {
          val authRequest =
               httpClientFactory.newRequest(HttpMethod.POST, "https://oauth.zaloapp.com/v2/mobile/validate_oauth_code")
          authRequest.addQueryStringParameter("app_id", "1829577289837795818")
          authRequest.addQueryStringParameter("version", "4.0")
          authRequest.addQueryStringParameter("frm", "sdk")
          authRequest.addQueryStringParameter(
               "code",
               "F7p3WdJVJWnYLfUqVCrj2aSQcCasat883X_Iuo-rIoLUAjURVQC691K2_-iKvG4hOsxlxbZTPJvvJ_tv1TfdHoDJl_Wzpcm93n6gtacbGG1sASNwRRz6EHmZoxe9tq9ULnhQZqJN25OnIFhgKfWbMKGRtPCi_1vwJIkVbaMnGbXDJiFe9RaW21atge51_MiK9XQKs02MDtaf6lc6Ai4KTIzspeq5psT0MbANaHJqEZcmscEnc7QGRlhf2hhwSe9lf99nWjmRbLlRb6Y7Y7kZIeI5TU1UMIRXxJPx5THkSm"
          )
          val serviceMapRequest = httpClientFactory.newRequest(HttpMethod.GET, SERVICE_MAP_URLS[0])

          val authHttpClient = HttpClient()
          val authResponse = authHttpClient.send(authRequest)

          val serviceMapHttpClient = HttpClient()
          val serviceMapResponse = serviceMapHttpClient.send(serviceMapRequest)

          val requestAuthText = authResponse.getText()
          val requestAuthStatusCode = authResponse.getStatusCode()

          val requestServiceMapText = serviceMapResponse.getText()
          val requestServiceMapStatusCode = serviceMapResponse.getStatusCode()

          assertThat(requestAuthStatusCode).isEqualTo(HttpURLConnection.HTTP_OK)
          assertThat(requestServiceMapStatusCode).isEqualTo(HttpURLConnection.HTTP_OK)

          assertThat(requestAuthText).isEqualTo(resultAuth)
          assertThat(requestServiceMapText).isEqualTo(resultServiceMap)
     }

     @Test
     fun `create get http request`()
     {
          val authRequest =
               httpClientFactory.newRequest(HttpMethod.GET, "https://oauth.zaloapp.com/v2/mobile/validate_oauth_code")
          authRequest.addQueryStringParameter("app_id", "1829577289837795818")
          authRequest.addQueryStringParameter("version", "4.0")
          authRequest.addQueryStringParameter("frm", "sdk")
          authRequest.addQueryStringParameter(
               "code",
               "F7p3WdJVJWnYLfUqVCrj2aSQcCasat883X_Iuo-rIoLUAjURVQC691K2_-iKvG4hOsxlxbZTPJvvJ_tv1TfdHoDJl_Wzpcm93n6gtacbGG1sASNwRRz6EHmZoxe9tq9ULnhQZqJN25OnIFhgKfWbMKGRtPCi_1vwJIkVbaMnGbXDJiFe9RaW21atge51_MiK9XQKs02MDtaf6lc6Ai4KTIzspeq5psT0MbANaHJqEZcmscEnc7QGRlhf2hhwSe9lf99nWjmRbLlRb6Y7Y7kZIeI5TU1UMIRXxJPx5THkSm"
          )

          val serviceMapRequest = httpClientFactory.newRequest(HttpMethod.GET, SERVICE_MAP_URLS[0])

          val authHttpClient = HttpClient()
          val authResponse = authHttpClient.send(authRequest)

          val serviceMapHttpClient = HttpClient()
          val serviceMapResponse = serviceMapHttpClient.send(serviceMapRequest)

          val requestAuthText = authResponse.getText()
          val requestAuthStatusCode = authResponse.getStatusCode()

          val requestServiceMapText = serviceMapResponse.getText()
          val requestServiceMapStatusCode = serviceMapResponse.getStatusCode()

          assertThat(requestAuthStatusCode).isEqualTo(HttpURLConnection.HTTP_OK)
          assertThat(requestServiceMapStatusCode).isEqualTo(HttpURLConnection.HTTP_OK)

          assertThat(requestAuthText).isEqualTo(resultAuth)
          assertThat(requestServiceMapText).isEqualTo(resultServiceMap)
     }
     
     
}