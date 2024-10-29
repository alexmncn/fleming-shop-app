package com.alexmncn.flemingshop.data.model

import java.math.BigInteger
import java.util.Date

data class Article(
    val ref: BigInteger,
    val codebar: BigInteger,
    val detalle: String,
    val codfam: Int,
    val pcosto: Float,
    val pvp: Float,
    val stock: Int,
    val factualizacion: Date,
    val destacado: Boolean,
    val hidden: Boolean
)