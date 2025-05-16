package data.remote.interceptor

import data.remote.retrofit.TokenPrefs
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenPrefs: TokenPrefs) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token: String? = tokenPrefs.token
        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Token", token)
        }
        return chain.proceed(requestBuilder.build())
    }
}