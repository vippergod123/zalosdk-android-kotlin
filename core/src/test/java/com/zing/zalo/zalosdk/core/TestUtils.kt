package com.zing.zalo.zalosdk.core

import org.robolectric.Robolectric

object TestUtils {
    fun waitTaskRunInBackgroundAndForeground() {
        Robolectric.flushBackgroundThreadScheduler()
        Robolectric.flushForegroundThreadScheduler()
    }


}

object TestData{

    const val INSTALL_APP= "{\n" +
            "  \"error\": 0,\n" +
            "  \"data\": {\n" +
            "    \"scanId\": \"3\",\n" +
            "    \"expiredTime\": 43200000,\n" +
            "    \"apps\": [\n" +
            "      \"com.zing.mp3\",\n" +
            "      \"com.zing.zalo\",\n" +
            "      \"com.facebook.orca\",\n" +
            "      \"com.facebook.katana\",\n" +
            "      \"com.sgiggle.production\",\n" +
            "      \"com.skype.raider\",\n" +
            "      \"com.viber.voip\",\n" +
            "      \"jp.naver.line.android\",\n" +
            "      \"com.tencent.mm\",\n" +
            "      \"com.whatsapp\",\n" +
            "      \"com.beetalk\",\n" +
            "      \"com.kakao.talk\",\n" +
            "      \"com.snapchat.android\",\n" +
            "      \"com.zing.tv3\",\n" +
            "      \"com.epi\",\n" +
            "      \"com.vng.inputmethod.labankey\",\n" +
            "      \"ht.nct\",\n" +
            "      \"sg.bigo.live\",\n" +
            "      \"com.campmobile.snow\",\n" +
            "      \"com.google.android.youtube\",\n" +
            "      \"com.google.android.inputmethod.latin\",\n" +
            "      \"com.touchtype.swiftkey\",\n" +
            "      \"com.nuance.swype\",\n" +
            "      \"bkav.android.inputmethod.gtv\",\n" +
            "      \"com.blackberry.keyboard\",\n" +
            "      \"kynam.ime.gotiengviet\",\n" +
            "      \"com.htc.sense.ime\",\n" +
            "      \"com.sec.android.inputmethod\",\n" +
            "      \"com.sonyericsson.textinput\",\n" +
            "      \"com.asus.ime\",\n" +
            "      \"com.nuance.swype.oppo\",\n" +
            "      \"com.lge.ime\",\n" +
            "      \"com.baidu.input_huawei\",\n" +
            "      \"com.android.inputmethod\",\n" +
            "      \"com.viettel.mocha.app\",\n" +
            "      \"com.vttm.keeng\",\n" +
            "      \"com.vttm.vietteldiscovery\",\n" +
            "      \"com.xb.topnews\",\n" +
            "      \"com.facebook.mlite\",\n" +
            "      \"com.facebook.lite\",\n" +
            "      \"com.vng.laban.gif\",\n" +
            "      \"com.goldsun.kola\",\n" +
            "      \"com.xb.vn_today\",\n" +
            "      \"com.google.android.apps.tachyon\",\n" +
            "      \"com.spotify.music\",\n" +
            "      \"com.google.duo\",\n" +
            "      \"com.ss.android.ugc.trill\",\n" +
            "      \"com.vttm.tinnganradio\"\n" +
            "    ]\n" +
            "  }\n" +
            "}"

    val INSTALLED_APP_LIST = arrayListOf("com.zing.mp3", "com.zing.zalo", "com.facebook.orca", "com.facebook.katana")

}
