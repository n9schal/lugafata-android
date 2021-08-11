package com.nischal.clothingstore.di

import com.apollographql.apollo.ApolloClient
import com.nischal.clothingstore.BuildConfig
import com.nischal.clothingstore.data.prefs.PrefsManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import java.util.concurrent.TimeUnit

val apiModule = module {
    single { createOkHttpClient(get()) }
    single { createApolloClient(get()) }
}

fun createOkHttpClient(prefsManager: PrefsManager): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    val authorizationInterceptor = object: Interceptor{
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + prefsManager.getToken() ?: "")
                .build()

            return chain.proceed(request)
        }
    }

    return OkHttpClient.Builder()
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .addInterceptor(authorizationInterceptor)
        .addInterceptor(logging)
        .build()
}

fun createApolloClient(okHttpClient: OkHttpClient): ApolloClient {
    return ApolloClient.builder()
        .serverUrl(BuildConfig.BASE_URL)
//        .subscriptionTransportFactory(WebSocketSubscriptionTransport.Factory("wss://apollo-fullstack-tutorial.herokuapp.com/graphql", okHttpClient))
        .okHttpClient(okHttpClient)
        .build()
}