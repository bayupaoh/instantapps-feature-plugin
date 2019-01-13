package com.suitmedia.sample

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco
import com.suitmedia.sample.core.BuildConfig
import com.suitmedia.sample.data.prefs.SuitPreferences
import com.suitmedia.sample.di.component.ApplicationComponent
import com.suitmedia.sample.di.component.DaggerApplicationComponent
import com.suitmedia.sample.di.module.ContextModule
//import io.realm.Realm
//import io.realm.RealmConfiguration

/**
 * Created by DODYDMW19 on 1/30/2018.
 */

class BaseApplication : MultiDexApplication() {

    companion object {
        lateinit var applicationComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder().contextModule(ContextModule(this)).build()

        Fresco.initialize(this)

        // Initial Preferences
        SuitPreferences.init(applicationContext, BuildConfig.ENCRYPT_KEY, true)

//        Realm.init(this)
//        val realmConfiguration = RealmConfiguration.Builder()
//                .schemaVersion(1)
//                .deleteRealmIfMigrationNeeded()
//                //.encryptionKey(CommonUtils.getKey()) // encrypt realm
//                .build()
//        Realm.setDefaultConfiguration(realmConfiguration)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}