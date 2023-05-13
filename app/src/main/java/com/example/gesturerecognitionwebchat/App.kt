package com.example.gesturerecognitionwebchat

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle

import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin


open class App : Application() {

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        startKoin {
            androidContext(this@App)
            modules(
                appModule,
                prefModule,
                apiModuleRetrofit,
                apiModuleOkHttp,
                repositoryModule,
                socketModule,
                peerModule
            )
        }
    }

}