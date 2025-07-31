package com.alexmncn.flemingshop.data.model

import java.math.BigInteger
import java.util.Date

data class Article(
    val ref: String,
    val codebar: String,
    val detalle: String,
    val codfam: Int,
    val pcosto: Float,
    val pvp: Float,
    val stock: Int,
    val factualizacion: String,
    val destacado: Boolean,
    val hidden: Boolean,
    val has_image: Boolean,
    val image_url: String
)