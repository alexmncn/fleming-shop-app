package com.alexmncn.flemingshop.data.network

import android.content.Context
import android.util.Log
import android.util.Base64
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object AuthManager {
    private var token: String? = null
    private var username: String? = null
    private var expiresAt: Long? = null


    // Inicializa el AuthManager cargando los datos de sesión desde SharedPreferences
    fun initialize(context: Context) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null)
        username = sharedPreferences.getString("username", null)
        expiresAt = sharedPreferences.getLong("expires_at", 0L)
    }

    // Verifica si el usuario está autenticado
    fun isAuthenticated(): Boolean {
        return !token.isNullOrEmpty() && expiresAt?.let { it > System.currentTimeMillis() } == true
    }

    fun getToken(): String? = token
    fun getUsername(): String? = username

    // Guarda la sesión de usuario en memoria y en SharedPreferences
    fun saveSession(context: Context, token: String) {
        val usernameFromToken = usernameFromToken(token)
        val expiresAtFromToken = expFromToken(token)

        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("token", token)
            putString("username", usernameFromToken)
            putLong("expires_at", expiresAtFromToken ?: 0L)
            apply()
        }

        this.token = token
        this.username = usernameFromToken
        this.expiresAt = expiresAtFromToken
    }

    // Limpia los datos de sesión (tanto en memoria como en SharedPreferences)
    fun clearSession(context: Context) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
        token = null
        username = null
        expiresAt = null
    }

    private fun decodeJwtPayload(token: String): JsonObject? {
        return try {
            val payload = token.split(".")[1]
            val decodedPayload = String(Base64.decode(payload, Base64.URL_SAFE), Charsets.UTF_8)
            JsonParser.parseString(decodedPayload).asJsonObject
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Extrae el nombre de usuario del token
    private fun usernameFromToken(token: String): String? {
        return try {
            val payload = decodeJwtPayload(token)
            payload?.getAsJsonObject("sub")?.get("username")?.asString
        } catch (e: Exception) {
            null
        }
    }

    // Extrae la fecha de expiración del token
    private fun expFromToken(token: String): Long? {
        return try {
            val payload = decodeJwtPayload(token)
            payload?.get("exp")?.asLong?.times(1000) // Convertir a milisegundos
        } catch (e: Exception) {
            null
        }
    }
}
