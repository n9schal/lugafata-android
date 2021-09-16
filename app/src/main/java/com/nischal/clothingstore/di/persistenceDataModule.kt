package com.nischal.clothingstore.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import com.nischal.clothingstore.data.db.AppDatabase
import com.nischal.clothingstore.data.db.dao.ShoppingListDao
import com.nischal.clothingstore.data.prefs.PrefsManager
import com.nischal.clothingstore.di.PersistenceDataSourceProperties.DB_NAME
import com.nischal.clothingstore.di.PersistenceDataSourceProperties.PREF_NAME
import org.koin.dsl.module.module

val persistenceDataModule = module {
    single { provideSharedPreference(get(), getProperty(PREF_NAME)) }
    single { providePrefsManager(get(), get()) }
    single { provideRoomDatabase(get(), getProperty(DB_NAME)) }
    single { provideShoppingListDao(get()) }
//    single { provideDeliveryLocationsDao(get())}
}

object PersistenceDataSourceProperties {
    /**
     *   use PREF_NAME for referencing to preference name in koin.properties
     * */
    const val PREF_NAME = "PREF_NAME"
    const val DB_NAME = "DB_NAME"
}

fun provideSharedPreference(context: Context, prefName: String): SharedPreferences {
    return context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
}

fun providePrefsManager(gson: Gson, pref: SharedPreferences) = PrefsManager(gson, pref)

fun provideRoomDatabase(context: Context, dbName: String): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, dbName)
        .fallbackToDestructiveMigration().build()
}

fun provideShoppingListDao(appDatabase: AppDatabase): ShoppingListDao = appDatabase.getShoppingListDao()
//
//fun provideDeliveryLocationsDao(appDatabase: AppDatabase): DeliveryLocationsDao = appDatabase.getDeliveryLocationsDao()