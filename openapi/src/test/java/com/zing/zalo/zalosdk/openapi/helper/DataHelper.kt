package com.zing.zalo.zalosdk.openapi.helper

object DataHelper {
    const val accessToken = "access_token"
    const val accessTokenData = "{\"data\":{\"access_token\":\"$accessToken\",\"expires_in\":\"3600\"},\"error\":0}"
    const val profile = "{\"birthday\":\"01\\/01\\/0001\",\"gender\":\"male\",\"name\":\"${User.name}\",\"id\":\"${User.id}\",\"picture\":{\"data\":{\"url\":\"https:\\/\\/s120.avatar.talk.zdn.vn\\/4\\/6\\/5\\/7\\/2\\/120\\/f720ee82b0e0c9b6cd4ead568a8e20f1.jpg\"}}}"
    const val listFriendsUsedApp = "{\"summary\":{\"total_count\":2},\"data\":[{\"gender\":\"male\",\"name\":\"Tri Tri Tri Tri Tri Tri\",\"id\":\"1491696566623706686\",\"picture\":{\"data\":{\"url\":\"https:\\/\\/s120.avatar.talk.zdn.vn\\/c\\/f\\/c\\/a\\/8\\/120\\/edd85e1543aa87986988bab3d95c4354.jpg\"}}},{\"gender\":\"male\",\"name\":\"Liem Vo\",\"id\":\"5905588571164089465\",\"picture\":{\"data\":{\"url\":\"https:\\/\\/s120.avatar.talk.zdn.vn\\/3\\/6\\/9\\/9\\/5\\/120\\/c2836edba274c3c7d15084138f782fa1.jpg\"}}}]}"
    const val sendMessageToFriend = "{\"to\":\"${User.id}\"}"
}

object User {
    const val name = "test_user"
    const val id = "123abc123"
}