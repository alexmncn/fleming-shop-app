package com.alexmncn.flemingshop.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleViewModel(private val repository: ArticleRepository) : ViewModel() {

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> get() = _articles

    private val _totalArticles = MutableLiveData<Int>()
    val totalArticles: LiveData<Int> get() = _totalArticles

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Obtener total de artículos
    fun fetchAllArticlesTotal() {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getAllArticlesTotal()
                }
                _totalArticles.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Obtener todos los artículos
    fun fetchAllArticles(page: Int = 1, perPage: Int = 20) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getAllArticles(page, perPage)
                }
                _articles.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}