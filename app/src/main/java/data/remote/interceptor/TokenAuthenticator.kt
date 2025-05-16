package data.remote.interceptor

import data.remote.retrofit.TokenPrefs
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenPrefs: TokenPrefs
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // при 401 делитится сохранённый токен
        runBlocking { tokenPrefs.clear() }
        // повтор запроса без заголовка, чтобы UI мог отловить 401 и показать экран логина
        return response.request
            .newBuilder()
            .removeHeader("Token")
            .build()
    }
}