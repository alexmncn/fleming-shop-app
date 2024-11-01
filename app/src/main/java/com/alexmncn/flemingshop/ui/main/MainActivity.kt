package com.alexmncn.flemingshop.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.alexmncn.flemingshop.R
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson = Gson()

        var featuredArticles: List<Article>
        val textView = findViewById<TextView>(R.id.textView)

        // Ejecutamos la solicitud en una corrutina
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiService.getFeaturedArticles()
            if (response != null && response.isSuccessful) {
                val responseData = response.body?.string()

                val listType = object : TypeToken<List<Article>>() {}.type
                featuredArticles = gson.fromJson(responseData, listType)

                val article1 = featuredArticles[0]

                Log.d("ApiService", "Articles: $featuredArticles")

                // Actualizamos el TextView en el hilo principal
                runOnUiThread {
                    textView.text = article1.toString()
                }
            } else {
                Log.e("ApiService", "Error: ${response?.code}")
            }
        }
    }
}