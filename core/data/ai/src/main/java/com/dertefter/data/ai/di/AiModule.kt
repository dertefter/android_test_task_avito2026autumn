package com.dertefter.data.ai.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.dertefter.data.ai.R
import com.dertefter.data.ai.api.AiApi
import com.dertefter.data.ai.api.AuthApi
import com.dertefter.data.ai.repository.AiRepository
import com.dertefter.data.ai.repository.AiRepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AiApiDataStore

@Module
@InstallIn(SingletonComponent::class)
abstract class AiRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAiRepository(
        aiRepositoryImpl: AiRepositoryImpl
    ): AiRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    @AiApiDataStore
    fun provideAiDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("ai_prefs") }
        )
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    private fun createOkHttpClientWithCerts(context: Context): OkHttpClient {
        // 1. Загружаем сертификаты из res/raw
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply { load(null, null) }

        // Список ваших сертификатов в res/raw
        val certResIds = listOf(R.raw.russian_trusted_root_ca, R.raw.russian_trusted_sub_ca)

        certResIds.forEachIndexed { index, resId ->
            context.resources.openRawResource(resId).use { it ->
                val certificate = certificateFactory.generateCertificate(it)
                keyStore.setCertificateEntry("cert_$index", certificate)
            }
        }

        // 2. Создаем TrustManager, который доверяет этим сертификатам
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(keyStore)
        val trustManagers = tmf.trustManagers
        val x509TrustManager = trustManagers[0] as X509TrustManager

        // 3. Создаем SSLContext
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, null)

        // 4. Настраиваем OkHttpClient
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, x509TrustManager)
            .build()
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return createOkHttpClientWithCerts(context)
    }


    @Provides
    @Singleton
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(json: Json, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://ngw.devices.sberbank.ru:9443/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    @Named("AiRetrofit")
    fun provideAiRetrofit(json: Json, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.giga.chat/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(@Named("AuthRetrofit") retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAiApi(@Named("AiRetrofit") retrofit: Retrofit): AiApi = retrofit.create(AiApi::class.java)
}
