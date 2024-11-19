package com.alexmncn.flemingshop.data.model

import java.util.Date

data class LoginResponse(
    val username: String,
    val token: String,
    val expiresAt: Date
)