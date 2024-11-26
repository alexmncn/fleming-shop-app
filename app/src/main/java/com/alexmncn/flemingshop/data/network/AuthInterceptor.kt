package com.alexmncn.flemingshop.data.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = AuthManager.getToken()

        // Si tenemos un token, se añade a la solicitud
        val authenticatedRequest = token?.let {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $it")
                .build()
        } ?: request

        val response = chain.proceed(authenticatedRequest)

        // Si el token expiró (401 Unauthorized), se limpia la sesión
        if (response.code == 401) {
            AuthManager.clearSession(context)
        }

        return response
    }
}