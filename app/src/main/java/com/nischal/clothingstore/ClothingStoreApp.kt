package com.nischal.clothingstore

import android.app.Application
import com.nischal.clothingstore.di.*
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class ClothingStoreApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin(
            this, listOf(
                apiModule,
                appModule,
                persistenceDataModule,
                repositoryModule,
                viewModelModule
            ), mapOf(), true
        )

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}