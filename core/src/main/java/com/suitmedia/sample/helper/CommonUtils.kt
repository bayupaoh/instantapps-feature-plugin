package com.suitmedia.sample.helper

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import com.suitmedia.sample.data.prefs.DataConstant
import com.suitmedia.sample.data.prefs.SuitPreferences
import java.security.SecureRandom

/**
 * Created by dodydmw19 on 7/18/18.
 */

class CommonUtils {

    companion object {

        fun checkTwitterApp(context: Context): Boolean {
            return try {
                var info = context.packageManager.getApplicationInfo("com.twitter.android", 0)
                true
            } catch (e: NameNotFoundException) {
                false
            }
        }

        fun openAppInStore(context: Context) {
            // you can also use BuildConfig.APPLICATION_ID
            try {
                val appId = context.packageName
                val rateIntent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appId"))
                var marketFound = false
                // find all applications able to handle our rateIntent
                val otherApps = context.packageManager
                        .queryIntentActivities(rateIntent, 0)
                for (otherApp in otherApps) {
                    // look for Google Play application
                    if (otherApp.activityInfo.applicationInfo.packageName == "com.android.vending") {

                        val otherAppActivity = otherApp.activityInfo
                        val componentName = ComponentName(
                                otherAppActivity.applicationInfo.packageName,
                                otherAppActivity.name
                        )
                        // make sure it does NOT open in the stack of your activity
                        rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        // task reparenting if needed
                        rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                        // if the Google Play was already open in a search result
                        //  this make sure it still go to the app page you requested
                        rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        // this make sure only the Google Play app is allowed to
                        // intercept the intent
                        rateIntent.component = componentName
                        context.startActivity(rateIntent)
                        marketFound = true
                        break

                    }
                }

                // if GP not present on device, open web browser
                if (!marketFound) {
                    val webIntent = Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appId"))
                    context.startActivity(webIntent)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun getKey() : ByteArray{
            // Generate a random encryption key
            val key = ByteArray(64)
            SecureRandom().nextBytes(key)

            return if(SuitPreferences.instance()?.getString(DataConstant.RANDOM_KEY) != null &&
                    SuitPreferences.instance()?.getString(DataConstant.RANDOM_KEY).toString().isNotEmpty()){
                SuitPreferences.instance()?.getObject(DataConstant.RANDOM_KEY, ByteArray::class.java)!!
            }else{
                SuitPreferences.instance()?.saveObject(DataConstant.RANDOM_KEY, key)
                key
            }

        }

    }

}