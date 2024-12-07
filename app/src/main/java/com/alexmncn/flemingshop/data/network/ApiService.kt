package com.alexmncn.flemingshop.data.network

import com.alexmncn.flemingshop.utils.Constans
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

class ApiService(private val client: OkHttpClient) {
    // Funcion HTTP GET que recibe la ruta, hace la peticion y devuelve la respuesta
    private fun  makeGetRequest(route: String): Response? {
        return try {
            val request = Request.Builder()
                .get()
                .url(Constans.BASE_URL+route)
                .build()

            client.newCall(request).execute()
        } catch (e: IOException) {
            null
        }
    }

    private fun makePostRequest(route: String, body: RequestBody? = null): Response? {
        return try {
            val emptyBody = ByteArray(0).toRequestBody(null, 0) // Cuerpo vacío
            val request = Request.Builder()
                .post(body?: emptyBody)
                .url(Constans.BASE_URL+route)
                .build()

            client.newCall(request).execute()
        } catch (e: IOException) {
            null
        }
    }

    // API ENDPOINTS

    // Catalog routes
    fun getAllArticlesTotal(): Response? {
        val route = "articles/total"
        return makeGetRequest(route)
    }

    fun getAllArticles(page: Int = 1, perPage: Int = 20): Response? {
        val route = "articles/all?page=$page&per_page=$perPage"
        return makeGetRequest(route)
    }

    fun getSearchArticlesTotal(search: String, filter: String = "detalle"): Response? {
        val route = "articles/search/total?search=$search&filter=$filter"
        return makeGetRequest(route)
    }

    fun getSearchArticles(page: Int = 1, perPage: Int = 20, search: String, filter: String = "detalle"): Response? {
        val route = "articles/search?page=$page&per_page=$perPage&search=$search&filter=$filter"
        return makeGetRequest(route)
    }

    fun getFeaturedArticlesTotal(): Response? {
        val route = "articles/featured/total"
        return makeGetRequest(route)
    }

    fun getFeaturedArticles(page: Int = 1, perPage: Int = 20): Response? {
        val route = "articles/featured?page=$page&per_page=$perPage"
        return makeGetRequest(route)
    }

    fun getNewArticlesTotal(): Response? {
        val route = "articles/new/total"
        return makeGetRequest(route)
    }

    fun getNewArticles(page: Int = 1, perPage: Int = 20): Response? {
        val route = "articles/new?page=$page&per_page=$perPage"
        return makeGetRequest(route)
    }

    fun getFamilies(): Response? {
        val route = "articles/families"
        return  makeGetRequest(route)
    }

    fun getFamilyArticlesTotal(familyId: Int): Response? {
        val route = "articles/families/$familyId/total"
        return makeGetRequest(route)
    }

    fun getFamilyArticles(page: Int = 1, perPage: Int = 20, familyId: Int): Response? {
        val route = "articles/families/$familyId?page=$page&per_page=$perPage"
        return makeGetRequest(route)
    }


    // Auth routes
    fun login(username: String, password: String): Response? {
        val route = "login"
        val jsonBody = JSONObject()
        jsonBody.put("username", username)
        jsonBody.put("password", password)

        // Crear el cuerpo de la solicitud con JSON
        val body = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
        return makePostRequest(route, body)
    }

    fun logout(): Response? {
        val route = "logout"
        return makePostRequest(route)
    }


    // Admin actions routes
    fun featureArticle(codebar: String, featured: Boolean = true): Response? {
        val route = "articles/feature?codebar=$codebar&featured=$featured"
        return makePostRequest(route)
    }

    fun hideArticle(codebar: String, hidden: Boolean = true): Response? {
        val route = "articles/hide?codebar=$codebar&hidden=$hidden"
        return makePostRequest(route)
    }

    fun hideFamily(codfam: String, hidden: Boolean, recursive: Boolean = true): Response? {
        val route = "articles/feature?codebar=$codfam&hidden=$hidden&recursive=$recursive"
        return makePostRequest(route)
    }

    fun hideFamilyArticles(codfam: String, hidden: Boolean = true): Response? {
        val route = "articles/hide?codebar=$codfam&hidden=$hidden"
        return makePostRequest(route)
    }

    fun uploadArticleImage(codebar: String, file: File): Response? {
        val route = "upload/articles/images?codebar=$codebar"
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
            .build()
        return makePostRequest(route, body)
    }
}
