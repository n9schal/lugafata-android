package com.nischal.clothingstore

import android.app.Application
import com.nischal.clothingstore.di.appModule
import com.nischal.clothingstore.di.persistenceDataModule
import com.nischal.clothingstore.di.repositoryModule
import com.nischal.clothingstore.di.viewModelModule
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class ClothingStoreApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin(
            this, listOf(
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