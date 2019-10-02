package com.zing.zalo.devicetrackingsdk

interface DeviceTrackingListener {
    fun onComplete(result: String?)
}

interface IDeviceTracking {
    fun setDeviceId(deviceId:String, expiredTime:String)
    fun getDeviceId(): String?
    fun getDeviceId(listener: DeviceTrackingListener?)
}

interface ISdkTracking {
    fun setSDKId(value: String)
    fun getSDKId(): String?

    fun getSDKId(listener: SdkTrackingListener?)

    fun setPrivateKey(value: String)
    fun getPrivateKey(): String?
}


interface SdkTrackingListener {
    fun onComplete(result: String?)
}