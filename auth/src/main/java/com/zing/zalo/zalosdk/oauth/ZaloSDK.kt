package com.zing.zalo.zalosdk.oauth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.zing.zalo.zalosdk.core.helper.AppInfo
import com.zing.zalo.zalosdk.core.helper.Utils
import com.zing.zalo.zalosdk.core.log.Log
import com.zing.zalo.zalosdk.oauth.callback.GetZaloLoginStatus
import com.zing.zalo.zalosdk.oauth.callback.ValidateOAuthCodeCallback
import com.zing.zalo.zalosdk.oauth.helper.AuthStorage

@SuppressLint("StaticFieldLeak")
object ZaloSDK {
    private var mAuthenticator: IAuthenticator? = null
    private var mStorage: AuthStorage? = null

    private var isInitialized = false
//    private var sdkTracking: SdkTracking? = null
//    private var deviceTracking: IDeviceTracking? = null


    /**
     * Initialize the SDK
     * @see com.zing.zalo.provider.ZaloBaseSDK.zaloAuthInit
     * method is called by reflection Kotlin (@see class above)
     */
    private fun initialize(context: Context) {
        if (isInitialized)
            return

        val ctx = context.applicationContext
        isInitialized = true
        mStorage = AuthStorage(ctx)
        mAuthenticator = Authenticator(ctx, mStorage!!)
        Log.d("ZaloSDK", "ZaloSDK isInitialized")
    }

    /**
     * Authenticate by using Zalo account
     * @param activity Activity the login activity
     * @param loginVia not support, SDK will login with Zalo app only.
     * @param listener AuthCompleteListenerI listener to receive authenticate event
     */
    fun authenticate(
        activity: Activity,
        loginVia: LoginVia,
        listener: IAuthenticateCompleteListener
    ) {
        if (checkInitialize())
            mAuthenticator?.authenticate(activity, loginVia, listener)
    }


    fun unAuthenticate() {
        if (checkInitialize())
            mAuthenticator?.unAuthenticate()
    }

    fun registerZalo(activity: Activity, listener: IAuthenticateCompleteListener) {
        if (checkInitialize())
            mAuthenticator?.registerZalo(activity, listener)
    }

    fun getZaloLoginStatus(callback: GetZaloLoginStatus) {
        if (checkInitialize())
            mAuthenticator?.getZaloLoginStatus(callback)
    }

    /**
     * Check if users have already authenticated.
     * @param callback Callback will be called after verify with server. If passed null, no server verification will be made.
     * @return True if oauth code cached, otherwise false
     */
    fun isAuthenticate(callback: ValidateOAuthCodeCallback): Boolean {
        if (checkInitialize()) {
            return mAuthenticator?.isAuthenticate(mStorage?.getOAuthCode().toString(), callback)!!
        }
        return false
    }



    fun getVersion(): String {
        return Constant.core.VERSION
    }

    /**
     * Set language for ZaloSDK
     * language: vi, my
     */
    private fun setLanguageSDK(language: String) {
        Utils.setLanguage(language)
    }

    fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        if (checkInitialize())
            return mAuthenticator?.onActivityResult(activity, requestCode, resultCode, data)!!
        return false
    }


    private fun checkInitialize(): Boolean {
        if (isInitialized && mAuthenticator != null)
            return true

        Log.e("Missing call declare com.zing.zalo.zalosdk.oauth.ZaloSDKApplication in Application or call wrap init")
        return false
    }


}