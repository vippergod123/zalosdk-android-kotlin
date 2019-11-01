package com.zing.zalo.zalosdk.demo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zing.zalo.zalosdk.openapi.ZaloOpenApi
import com.zing.zalo.zalosdk.openapi.ZaloOpenApiCallback
import org.json.JSONObject

class OpenApiActivity : AppCompatActivity(), ZaloOpenApiCallback {
    override fun onResult(data: JSONObject?) {
        callBackTextView.text = data.toString()
    }

    private lateinit var getProfileButton: Button
    private lateinit var getFriendListUsedAppButton: Button
    private lateinit var getFriendListInvitableButton: Button
    private lateinit var inviteFriendUseAppButton: Button
    private lateinit var postToWallButton: Button
    private lateinit var sendMsgToFriendButton: Button

    private lateinit var callBackTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_api)
        bindUI()
        configureUI()
        bindViewsListener()
    }

    //#region private supportive method
    private fun bindUI() {
        getProfileButton = findViewById(R.id.get_profile_button)
        getFriendListUsedAppButton = findViewById(R.id.get_friend_list_used_app_button)
        getFriendListInvitableButton = findViewById(R.id.get_friend_list_invitable_button)
        inviteFriendUseAppButton = findViewById(R.id.invite_friend_use_app_button)
        postToWallButton = findViewById(R.id.post_to_wall_button)
        sendMsgToFriendButton = findViewById(R.id.send_message_to_friend_button)

        callBackTextView = findViewById(R.id.callback_text_view)
    }

    private fun configureUI() {

    }

    private fun bindViewsListener() {
        getProfileButton.setOnClickListener {
            val fields = arrayOf("id", "birthday", "gender", "picture", "name")
            ZaloOpenApi.getProfile(fields, this)
        }
        getFriendListUsedAppButton.setOnClickListener {
            val fields = arrayOf("id", "name", "gender", "picture")
            ZaloOpenApi.getFriendListUsedApp(fields, 0, 999, this)
        }
        getFriendListInvitableButton.setOnClickListener {
            val fields = arrayOf("id", "name", "gender", "picture")
            ZaloOpenApi.getFriendListInvitable(this, 0, 999, this, fields)

        }
        inviteFriendUseAppButton.setOnClickListener {
            val friendsList = arrayOf("")
            ZaloOpenApi.inviteFriendUseApp(friendsList, "Hello!", this)
        }
        postToWallButton.setOnClickListener {
            ZaloOpenApi.postToWall(
                "http://vnexpress.net",
                "http://vnexpress.net",
                this
            )
        }
        sendMsgToFriendButton.setOnClickListener {
            ZaloOpenApi.sendMsgToFriend("1491696566623706686", "msg", "http://vnexpress.net", this)
        }
    }
    //#endregion
}
