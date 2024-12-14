package com.alexmncn.flemingshop.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexmncn.flemingshop.data.db.ArticleItem
import com.alexmncn.flemingshop.data.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigInteger


class ShoppingListViewModel(repository: ShoppingListRepository) : ViewModel() {

    private val shoppingListRepository = repository

    // Lista de artículos como un flujo de estado
    private val _articleItems = MutableStateFlow<List<ArticleItem>>(emptyList())
    val articleItems: StateFlow<List<ArticleItem>> = _articleItems

    // Cargar los artículos desde el repositorio
    init {
        viewModelScope.launch {
            shoppingListRepository.getAllArticles().collect { articles ->
                _articleItems.value = articles
            }
        }
    }

    // Eliminar todos los artículos
    fun deleteAllArticles() {
        viewModelScope.launch {
            shoppingListRepository.deleteAllArticles()
        }
    }

    // Obtener un solo artículo por su codebar
    fun getArticleByCodebar(codebar: BigInteger): StateFlow<ArticleItem?> {
        val articleItemFlow = MutableStateFlow<ArticleItem?>(null)
        viewModelScope.launch {
            shoppingListRepository.getArticleByCodebar(codebar).collect { article ->
                articleItemFlow.value = article
            }
        }
        return articleItemFlow
    }

    // Devuelve la cantidad de un artículo (0 si no existe)
    fun getArticleQuantityByCodebar(codebar: BigInteger): StateFlow<Int> {
        return shoppingListRepository.getArticleByCodebar(codebar)
            .map { article -> article?.quantity ?: 0 } // Mapea el artículo para devolver 0 si es null
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = 0 // Valor inicial
            )
    }

    // Eliminar un artículo de la lista
    fun deleteArticleByCodebar(codebar: BigInteger) {
        viewModelScope.launch {
            shoppingListRepository.deleteArticleByCodebar(codebar)
        }
    }

    // Agregar un nuevo artículo
    fun insertArticle(articleItem: ArticleItem) {
        viewModelScope.launch {
            shoppingListRepository.insertArticle(articleItem)
        }
    }
}
