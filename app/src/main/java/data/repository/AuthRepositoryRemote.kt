package data.repository

import data.remote.dto.LoginRequest
import data.remote.mesa.MesaApi
import data.remote.retrofit.TokenPrefs
import retrofit2.HttpException

class AuthRepositoryRemote (private val api: MesaApi, private val prefs: TokenPrefs) {
    suspend fun login(login: String, password: String): Boolean {
        return try {
            val response = api.login(LoginRequest(login, password))
            prefs.save(response.token)
            true
        } catch (e: HttpException) {
            prefs.clear()
            false
        }
    }

    suspend fun logout() {
        api.logout()
        prefs.clear()
    }
}