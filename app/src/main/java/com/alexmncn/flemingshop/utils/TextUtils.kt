package com.alexmncn.flemingshop.utils

import java.util.Locale

fun capitalizeText(value: String?): String {
    if (value.isNullOrEmpty()) return value ?: ""

    return value.lowercase(Locale.ROOT)
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}
