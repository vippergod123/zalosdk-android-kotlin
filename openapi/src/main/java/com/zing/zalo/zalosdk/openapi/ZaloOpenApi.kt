package com.zing.zalo.zalosdk.openapi

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import androidx.annotation.Nullable
import com.zing.zalo.zalosdk.core.Constant
import com.zing.zalo.zalosdk.core.http.HttpGetRequest
import com.zing.zalo.zalosdk.core.http.HttpUrlEncodedRequest
import com.zing.zalo.zalosdk.core.http.IHttpRequest
import com.zing.zalo.zalosdk.core.log.Log
import com.zing.zalo.zalosdk.core.module.BaseModule
import com.zing.zalo.zalosdk.core.module.ModuleManager
import com.zing.zalo.zalosdk.oauth.helper.AuthStorage
import org.json.JSONObject
import java.lang.ref.WeakReference

@SuppressLint("StaticFieldLeak")
class ZaloOpenApi : BaseModule() {
    companion object {
        private val instance = ZaloOpenApi()
        private lateinit var authStorage: AuthStorage

        fun getInstance(): ZaloOpenApi {
            return instance
        }

        init {
            ModuleManager.addModule(instance)
        }
    }


    private var accessToken = ""
    private var expiredAccessToken = 0L

    internal lateinit var callApiAsyncTask: CallApiAsyncTask
    internal lateinit var getAccessTokenAsyncTask: GetAccessTokenAsyncTask

    override fun onStart(context: Context) {
        super.onStart(context)
        authStorage = AuthStorage(context.applicationContext)
        updateAccessToken()
    }

    /**
     * Get Zalo user's profile
     * http://developers.zaloapp.com/docs/api/open-api/tai-lieu/thong-tin-nguoi-dung-post-28
     * @param fields   : id, birthday, gender, picture, name ex: {"id", "birthday", "gender", "picture", "name"}
     * @param callback
     */
    fun getProfile(fields: Array<String>, @Nullable callback: ZaloOpenApiCallback) {
        if (!checkInitialize()) return

        val request = HttpGetRequest(Constant.api.GRAPH_V2_ME_PATH)
        request.addQueryStringParameter("fields", buildFieldsParam(fields))
        callApi(request, callback)
    }

    /**
     * Lấy danh sách tất cả bạn bè của người dùng đã sử dụng ứng dụng
     * http://developers.zaloapp.com/docs/api/open-api/tai-lieu/danh-sach-ban-be-post-34
     * @param fields   : Hỗ trợ các field: id, name, picture, gender
     * @param position position
     * @param count    count
     * @param callback
     */
    fun getFriendListUsedApp(
        fields: Array<String>,
        position: Int,
        count: Int,
        @Nullable callback: ZaloOpenApiCallback
    ) {
        if (!checkInitialize()) return
        val request = HttpGetRequest(Constant.api.GRAPH_ME_FRIENDS_PATH)
        request.addQueryStringParameter("fields", buildFieldsParam(fields))
        request.addQueryStringParameter("offset", position.toString())
        request.addQueryStringParameter("limit", count.toString())
        callApi(request, callback)
    }


    /**
     * Lấy danh sách bạn bè chưa sử dụng ứng dụng và có thể nhắn tin mời sử dụng ứng dụng
     * http://developers.zaloapp.com/docs/api/open-api/tai-lieu/danh-sach-ban-be-post-34
     *
     * @param position position
     * @param count    count
     * @param callback
     * @param fields   : Hỗ trợ các field: id, name, picture, gender
     */
    fun getFriendListInvitable(
        fields: Array<String>,
        position: Int,
        count: Int,
        @Nullable callback: ZaloOpenApiCallback
    ) {
        if (!checkInitialize()) return
        val request = HttpGetRequest(Constant.api.GRAPH_ME_INVITABLE_FRIENDS_PATH)
        request.addQueryStringParameter("fields", buildFieldsParam(fields))
        request.addQueryStringParameter("offset", position.toString() + "")
        request.addQueryStringParameter("limit", count.toString() + "")
        callApi(request, callback)
    }


    /**
     * http://developers.zaloapp.com/docs/api/open-api/tai-lieu/moi-su-dung-ung-dung-post-41
     * @param friendId ex: {"friend-id1", "friend-id2", "friend-id3"}
     * @param message  String
     * @param callback ZaloOpenApiCallback
     */
    fun inviteFriendUseApp(
        friendId: Array<String>,
        message: String,
        @Nullable callback: ZaloOpenApiCallback
    ) {
        if (!checkInitialize()) return

        val request = HttpUrlEncodedRequest(Constant.api.GRAPH_APP_REQUESTS_PATH)
        request.addParameter("to", buildFieldsParam(friendId))
        request.addParameter("message", message)
        callApi(request, callback)
    }


    /**
     * Post a feed to wall
     * http://developers.zaloapp.com/docs/api/open-api/tai-lieu/dang-bai-viet-post-39
     * @param link     String url link
     * @param msg      String msg
     * @param callback ZaloOpenApiCallback
     */
    fun postToWall(link: String, msg: String, @Nullable callback: ZaloOpenApiCallback) {
        if (!checkInitialize()) return

        val request = HttpUrlEncodedRequest(Constant.api.GRAPH_ME_FEED_PATH)
        request.addParameter("link", link)
        request.addParameter("message", msg)
        callApi(request, callback)
    }

    /**
     * Send message to friend
     * http://developers.zaloapp.com/docs/api/open-api/tai-lieu/goi-tin-nhan-toi-ban-be-post-1183
     * @param friendId Friend ID
     * @param msg      String content message
     * @param link     Link
     * @param callback ZaloOpenApiCallback
     */

    fun sendMsgToFriend(
        friendId: String,
        msg: String,
        link: String,
        @Nullable callback: ZaloOpenApiCallback
    ) {
        if (!checkInitialize()) return

        val request = HttpUrlEncodedRequest(Constant.api.GRAPH_ME_MESSAGE_PATH)
        request.addParameter("to", friendId)
        request.addParameter("message", msg)
        request.addParameter("link", link)
        callApi(request, callback)
    }


    //#region private supportive method
    private val enableUnitTest by lazy { false }

    private fun callApi(request: IHttpRequest, @Nullable callback: ZaloOpenApiCallback) {


        val tokenCallback = object : ZaloOpenApiCallback {
            override fun onResult(data: JSONObject?) {

                val error = data?.optInt("error", -1)
                if (error != 0) {
                    callback.onResult(data)
                    return
                }
                updateAccessToken()
                callApiAsyncTaskMethod(request)

            }
        }
        if (!enableUnitTest)
            callApiAsyncTask = CallApiAsyncTask(callback)

        if (isAccessTokenValid()) {
            callApiAsyncTaskMethod(request)
            return
        }

        if (!enableUnitTest)
            getAccessTokenAsyncTask = GetAccessTokenAsyncTask(
                WeakReference(getInstance().context as Context),
                tokenCallback
            )
        getAccessTokenAsyncTask.callback = tokenCallback
        getAccessTokenAsyncTask.execute()
    }

    private fun isAccessTokenValid(): Boolean {

        if (!TextUtils.isEmpty(accessToken) && expiredAccessToken > System.currentTimeMillis()) return true

        Log.w("isAccessTokenValid", "Token is not valid")
        return false
    }

    private fun buildFieldsParam(fields: Array<String>): String {
        if (fields.isNotEmpty()) {
            val param = StringBuffer()
            for (each in fields) {
                param.append(each).append(",")
            }
            return param.substring(0, param.length - 1)
        }
        return ""
    }

    private fun checkInitialize(): Boolean {
        if (getInstance().hasContext)
            return true

        Log.e("Zalo Open Api is not init yet!")
        return false
    }

    private fun updateAccessToken() {
        val accessTokenJSON = authStorage.getAccessTokenNewAPI()

        accessToken = accessTokenJSON?.optString("access_token") ?: ""
        expiredAccessToken = accessTokenJSON?.optLong("expires_in") ?: 0L

    }

    private fun callApiAsyncTaskMethod(request: IHttpRequest) {
        request.addQueryStringParameter("access_token", accessToken)
        callApiAsyncTask.execute(request)
    }

    //#endregion
}
