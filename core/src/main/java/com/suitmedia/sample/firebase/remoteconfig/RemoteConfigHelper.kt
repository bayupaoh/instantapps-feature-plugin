package com.suitmedia.sample.firebase.remoteconfig

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Window
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.suitmedia.sample.core.BuildConfig
import com.suitmedia.sample.core.R
import com.suitmedia.sample.helper.CommonConstant
import com.suitmedia.sample.helper.CommonUtils

/**
 * Created by dodydmw19 on 9/27/18.
 */

/**
 *
 *  Field Params :
 *  force_message -> for message content force update
 *  info_message -> for message content info update (can deny)
 *  minumum_force_android -> latest versionCode for force update
 *  minimum_info_android -> latest versionCode for info update
 *
 */

class RemoteConfigHelper {

    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    fun initialize(activity: Activity?) {
        if (activity != null) {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build()

            mFirebaseRemoteConfig?.setConfigSettings(configSettings)

            var cacheExpiration: Long = 0 // 1 hour in seconds.

            if (mFirebaseRemoteConfig?.info?.configSettings?.isDeveloperModeEnabled!!) {
                cacheExpiration = 0
            }

            mFirebaseRemoteConfig?.setDefaults(R.xml.remote_config_defaults)

            mFirebaseRemoteConfig?.fetch(cacheExpiration)
                    ?.addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig?.activateFetched()
                        }
                        showDialogUpdates(activity)
                    }
        }
    }

    private fun showDialogUpdates(activity: Activity?) {
        if (activity != null) {
            val currentVersion = java.lang.Double.valueOf(BuildConfig.VERSION_CODE.toDouble())
            var normalUpdateVersion: Double? = 0.0
            var forceUpdateVersion: Double? = 0.0

            if (mFirebaseRemoteConfig?.getString(CommonConstant.NOTIFY_FORCE_UPDATE) != null && !mFirebaseRemoteConfig?.getString(CommonConstant.NOTIFY_FORCE_UPDATE)!!.isEmpty()) {
                val info = mFirebaseRemoteConfig?.getString(CommonConstant.NOTIFY_FORCE_UPDATE)
                forceUpdateVersion = if (info != null) {
                    java.lang.Double.parseDouble(info)
                } else {
                    0.0
                }
            }

            if (mFirebaseRemoteConfig?.getString(CommonConstant.NOTIFY_NORMAL_UPDATE) != null && !mFirebaseRemoteConfig?.getString(CommonConstant.NOTIFY_NORMAL_UPDATE)!!.isEmpty()) {
                normalUpdateVersion = try {
                    val info = mFirebaseRemoteConfig?.getString(CommonConstant.NOTIFY_NORMAL_UPDATE)
                    if (info != null) {
                        java.lang.Double.parseDouble(info)
                    } else {
                        0.0
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    0.0
                }
            }

            val messages: String

            if (forceUpdateVersion != 0.0 && currentVersion < forceUpdateVersion!!) {
                try {
                    messages = mFirebaseRemoteConfig?.getString(CommonConstant.NOTIFY_FORCE_MESSAGE).toString()
                    val confirmDialog = AlertDialog.Builder(activity)
                            .setCancelable(false)
                            .setMessage(messages)
                            .setPositiveButton("OK") { d, _ ->
                                d.dismiss()
                                CommonUtils.openAppInStore(activity)
                            }
                            .create()

                    confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    confirmDialog.show()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return
            }

            if (normalUpdateVersion != 0.0 && currentVersion < normalUpdateVersion!!) {
                try {
                    messages = mFirebaseRemoteConfig?.getString(CommonConstant.NOTIFY_NORMAL_MESSAGE).toString()

                    val confirmDialog = AlertDialog.Builder(activity)
                            .setCancelable(false)
                            .setMessage(messages)
                            .setPositiveButton("OK") { d, _ ->
                                d.dismiss()
                                CommonUtils.openAppInStore(activity)
                            }
                            .setNegativeButton("CANCEL") { d, _ -> d.dismiss() }
                            .create()

                    confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    confirmDialog.show()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return
            }
        }
    }

    companion object {

        private var sHelper: RemoteConfigHelper? = null

        fun instance(): RemoteConfigHelper {
            if (sHelper == null) {
                sHelper = RemoteConfigHelper()
            }
            return sHelper as RemoteConfigHelper
        }
    }
}