package com.alexmncn.flemingshop.data.repository

import com.alexmncn.flemingshop.data.model.LoginResponse
import com.alexmncn.flemingshop.data.network.ApiService
import com.google.gson.Gson

class AuthRepository(private val apiService: ApiService) {
    private val gson = Gson()

    // AUTH func
    fun login(username: String, password: String, turnstileToken: String): LoginResponse {
        val response = apiService.login(username, password, turnstileToken)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return gson.fromJson(responseData, LoginResponse::class.java)
        } else if (response?.code == 401) {
            throw Exception("Unauthorized: Usuario o contraseña incorrectos")
        } else {
            throw Exception("Failed to login: ${response?.message}")
        }
    }

    fun logout(): String? {
        val response = apiService.logout()
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return responseData
        } else {
            throw Exception("Failed to logout: ${response?.message}")
        }
    }
}