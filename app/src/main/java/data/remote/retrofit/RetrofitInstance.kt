package data.remote.retrofit

import android.content.Context
import data.remote.interceptor.AuthInterceptor
import data.remote.interceptor.TokenAuthenticator
import data.remote.mesa.MesaApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitInstance {
    private lateinit var apiImpl: MesaApi

    fun init(context: Context) {
        val json = Json { ignoreUnknownKeys = true }
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(TokenPrefs(context)))
            .authenticator(TokenAuthenticator(TokenPrefs(context)))
            .build()
        apiImpl = Retrofit.Builder()
            .baseUrl("https://mesa.server")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MesaApi::class.java)
    }

    val api: MesaApi
        get() = if (::apiImpl.isInitialized) apiImpl
        else throw IllegalStateException("Call RetrofitInstance.init(context) first")
}