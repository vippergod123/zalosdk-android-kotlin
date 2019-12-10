package com.zing.zalo.zalosdk.openapi

import com.zing.zalo.zalosdk.openapi.model.FeedData
import org.jetbrains.annotations.Nullable

interface IZaloOpenApi {
    fun getProfile(fields: Array<String>, @Nullable callback: ZaloOpenApiCallback)

    fun getFriendListUsedApp(
        fields: Array<String>,
        position: Int,
        count: Int,
        @Nullable callback: ZaloOpenApiCallback
    )

    fun getFriendListInvitable(
        fields: Array<String>,
        position: Int,
        count: Int,
        @Nullable callback: ZaloOpenApiCallback
    )

    fun inviteFriendUseApp(
        friendId: Array<String>,
        message: String,
        @Nullable callback: ZaloOpenApiCallback
    )

    fun postToWall(link: String, msg: String, @Nullable callback: ZaloOpenApiCallback)
    fun sendMsgToFriend(
        friendId: String,
        msg: String,
        link: String,
        @Nullable callback: ZaloOpenApiCallback
    )

    fun shareMessage(
        feedData: FeedData,
        callback: ZaloPluginCallback
    )

    fun shareFeed(
        feedData: FeedData,
        callback: ZaloPluginCallback
    )
}