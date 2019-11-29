# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.zing.zalo.zalosdk.openapi.ZaloOpenApi { com.zing.zalo.zalosdk.openapi.GetAccessTokenAsyncTask getAccessTokenAsyncTask; }
-keep class com.zing.zalo.zalosdk.openapi.model.** { *; }
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ComponentName
-keep public class * extends android.app.backup.Context
-keep public class * extends android.content.Intent
-keep public class * extends android.app.backup.IntentFilter

-keepclassmembers class com.zing.zalo.zalosdk.openapi.ZaloOpenApi {
    public *;
}
-keepclassmembers class com.zing.zalo.zalosdk.openapi.exception.OpenApiException { *; }

-keepclasseswithmembers public interface androidx.annotation.Nullable { *;}
# Output a source map file
-printmapping proguard.map
