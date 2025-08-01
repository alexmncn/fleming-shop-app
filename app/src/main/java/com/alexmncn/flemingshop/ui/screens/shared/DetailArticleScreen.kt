package com.alexmncn.flemingshop.ui.screens.shared

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexmncn.flemingshop.data.db.AppDatabase
import com.alexmncn.flemingshop.data.db.ArticleItem
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.network.AuthManager
import com.alexmncn.flemingshop.data.repository.AdminActionsRepository
import com.alexmncn.flemingshop.data.repository.CatalogRepository
import com.alexmncn.flemingshop.data.repository.ShoppingListRepository
import com.alexmncn.flemingshop.data.viewmodel.ShoppingListViewModel
import com.alexmncn.flemingshop.data.viewmodel.ShoppingListViewModelFactory
import com.alexmncn.flemingshop.ui.components.FeaturedLabel
import com.alexmncn.flemingshop.ui.components.HiddenLabel
import com.alexmncn.flemingshop.ui.components.StockLabel
import com.alexmncn.flemingshop.utils.Constans
import com.alexmncn.flemingshop.utils.capitalizeText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger

@Composable
fun DetailArticleScreen(codebar: String, navController: NavController, db: AppDatabase) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val catalogRepository: CatalogRepository by lazy { CatalogRepository(ApiService(apiClient)) }
    val adminActionsRepository: AdminActionsRepository by lazy { AdminActionsRepository(ApiService(apiClient)) }
    val shoppingListRepository: ShoppingListRepository by lazy { ShoppingListRepository(db) }
    val shoppingListViewModel: ShoppingListViewModel = viewModel(factory = ShoppingListViewModelFactory(shoppingListRepository))

    var article by remember { mutableStateOf<Article?>(null) }
    var tags by remember { mutableIntStateOf(0) }
    var uploadingImg by remember { mutableStateOf(false) }
    var imgUploadSuccess by remember { mutableStateOf(false) }
    var addShoppListUnfold by remember { mutableStateOf(false) }
    val quantityInShoppingList by shoppingListViewModel.getArticleQuantityByCodebar(codebar).collectAsState(initial = 0)
    var lastQuantityInShoppingList by remember { mutableIntStateOf(quantityInShoppingList) }

    fun checkTags(article: Article?) {
        if (article != null) {
            // Comprobamos las etiquetas
            // Oculto
            if (article.hidden) {
                tags++
            }

            // Destacado
            if (article.destacado) {
                tags++
            }
        }
    }

    // Funcion para cargar articulos, por pagina
    fun loadArticle() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                article = catalogRepository.getSearchArticles(search = codebar, filter = "codebar")[0]

                checkTags(article) // Comprobamos las etiquetas
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
        }
    }

    // Llama a la función de actualización cuando se crea la actividad
    LaunchedEffect(Unit) {
        loadArticle()
    }


    // Al destacar el articulo
    fun onFeature() {
        var messageStatus = ""
        CoroutineScope(Dispatchers.IO).launch {
            if (article!!.destacado) { // Si está destacado, lo quita

                try {
                    val response =
                        adminActionsRepository.unfeatureArticle(article!!.codebar.toString())
                    if (response) {
                        article = article!!.copy(destacado = false) // Actualiza el estado local del parametro
                        checkTags(article) // Actualiza las etiquetas
                        messageStatus = "Articulo eliminado de destacados"
                    } else {
                        messageStatus = "Error al eliminar el articulo de destacados"
                    }
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                    messageStatus = "Error desconocido al eliminar el articulo de destacados"
                }
            } else if (!article!!.destacado) { // Si no lo está, lo destaca
                try {
                    val response =
                        adminActionsRepository.featureArticle(article!!.codebar.toString())
                    if (response) {
                        article = article!!.copy(destacado = true)
                        checkTags(article)
                        messageStatus = "Se ha destacado el artículo"
                    } else {
                        messageStatus = "Error al destacar el articulo"
                    }
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                    messageStatus = "Error desconocido al destacar el articulo"
                }
            }

            // Muestra el mensaje de estado
            withContext(Dispatchers.Main) {
                Toast.makeText(context, messageStatus, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Al ocultar el artículp
    fun onHide() {
        var messageStatus = ""
        CoroutineScope(Dispatchers.IO).launch {
            if (article!!.hidden) { // Si está oculto, lo quita
                try {
                    val response = adminActionsRepository.unhideArticle(article!!.codebar.toString())
                    if (response) {
                        article = article!!.copy(hidden = false) // Actualiza el estado local del parametro
                        checkTags(article) // Actualiza las etiquetas
                        messageStatus = "Articulo eliminado de ocultos"
                    } else {
                        Toast.makeText(context, "Error al eliminar el articulo de ocultos", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception) {
                    Log.e("error", e.toString())
                    messageStatus = "Error desconocido al eliminar el articulo"
                }
            } else if (!article!!.hidden) { // Si no lo está, lo oculta
                try {
                    val response = adminActionsRepository.hideArticle(article!!.codebar.toString())
                    if (response) {
                        article = article!!.copy(hidden = true)
                        checkTags(article)
                        messageStatus = "Se ha ocultado el artículo"
                    } else {
                        messageStatus = "Error al ocultar el articulo"
                    }
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                    messageStatus = "Error desconocido al ocultar el articulo"
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, messageStatus, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Sube la imagen seleccionada/tomada
    fun uploadImage(bitmap: Bitmap) {
        var messageStatus = ""
        CoroutineScope(Dispatchers.IO).launch {
            try {
                uploadingImg = true
                adminActionsRepository.uploadArticleImage(article!!.codebar.toString(), bitmap, context)
                messageStatus = "Imagen subida correctamente"
                uploadingImg = false
                imgUploadSuccess = true
            } catch (e: Exception) {
                Log.e("error", e.toString())
                messageStatus = "Error al subir la imagen"
                uploadingImg = false
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, messageStatus, Toast.LENGTH_SHORT).show()
            }
            if (imgUploadSuccess) {
                delay(2000)
                imgUploadSuccess = false
            }
        }
    }

    // Seleccionar imagen de galería
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            uploadImage(bitmap)
        }
    }

    // Tomar foto
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            uploadImage(it)
        }
    }

    // Al subir imagen del articulo
    fun onUploadImage() {
        val options = arrayOf("Tomar foto", "De Galería")
        AlertDialog.Builder(context)
            .setTitle("Seleccionar imagen")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePictureLauncher.launch(null) // Abrir cámara
                    1 -> pickImageLauncher.launch("image/*") // Seleccionar desde galería
                }
            }
            .show()
    }

    fun addShoppingList() {
        if (addShoppListUnfold) {
            // Creamos el articleItem
            val newArticleItem = ArticleItem(article!!.codebar, article!!.detalle, article!!.pvp, 1)
            shoppingListViewModel.insertArticle(newArticleItem)

            lastQuantityInShoppingList = quantityInShoppingList
        } else {
            // Desplegar
            addShoppListUnfold = true
        }
    }

    fun removeShoppingList(delete: Boolean = false) {
        if (addShoppListUnfold) {
            if (delete) {
                // Borramos el articulo de la lista
                shoppingListViewModel.deleteArticleByCodebar(article!!.codebar)

                addShoppListUnfold = false
                lastQuantityInShoppingList = -1
            } else {
                // Creamos el articleItem (Con cantidad negativa, para restar una unidad)
                val newArticleItem = ArticleItem(article!!.codebar, article!!.detalle, article!!.pvp, -1)
                shoppingListViewModel.insertArticle(newArticleItem)

                lastQuantityInShoppingList = quantityInShoppingList
            }
        } else {
            // Desplegar
            addShoppListUnfold = true
        }
    }


    article?.let {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(article!!.image_url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen del artículo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                contentScale = ContentScale.FillWidth
            )

            if (tags > 0) {
                Spacer(modifier = Modifier.height(10.dp))

                // Fila de Etiquetas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Oculto
                    if (article!!.hidden) {
                        HiddenLabel(modifier = Modifier.shadow(2.dp, shape = RoundedCornerShape(20.dp)))
                    }

                    // Destacado
                    if (article!!.destacado) {
                        FeaturedLabel(modifier = Modifier.shadow(2.dp, shape = RoundedCornerShape(20.dp)), navController)
                    }
                }
            }

            // Detalles del articulo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            ) {
                Text(
                    text = capitalizeText(article?.detalle),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Ref: " + article?.ref.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Cod: " + article?.codebar.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                if (AuthManager.isAuthenticated()) {
                    Text(
                        text = "Stock: " + article?.stock.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .padding(top = 6.dp, bottom = 2.dp)
                        .align(Alignment.Start)
                ) {
                    Text(
                        text = article?.pvp.toString() + " €",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Start)
                ) {
                    // Etiqueta de Stock
                    StockLabel(article!!.stock, modifier = Modifier)
                }
            }

            // Shopping list control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier
                    .wrapContentWidth() // Ajusta el ancho al contenido
                    .align(Alignment.End) // Lleva el Row al extremo derecho
                    .then(if (addShoppListUnfold) Modifier.fillMaxWidth() else Modifier)
            ) {
                // Add to shopping list button
                Card(
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = tween(durationMillis = 300)
                        )
                        .then(if (addShoppListUnfold) Modifier.weight(1f) else Modifier)
                        .padding(start = 10.dp, end = if (addShoppListUnfold) 5.dp else 10.dp, top = 10.dp, bottom = 10.dp)
                        .align(Alignment.CenterVertically) // Fijado a la derecha (si esta plegado)
                        .shadow(6.dp, shape = RoundedCornerShape(10.dp))
                        .clickable{
                            // Desplegar (controlado  por la func)
                            if (!addShoppListUnfold || lastQuantityInShoppingList == -1 || quantityInShoppingList <= 0) addShoppingList()
                                  },
                    colors = CardDefaults.cardColors(
                        containerColor = if (quantityInShoppingList > 0 && lastQuantityInShoppingList != -1) {
                            MaterialTheme.colorScheme.onPrimary
                        } else MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            // Ocupa todo el ancho si esta desplegado
                            .then(if (addShoppListUnfold) Modifier.fillMaxWidth() else Modifier)
                            .padding(10.dp)
                    ) {
                        if (quantityInShoppingList <= 0 || lastQuantityInShoppingList == -1) { // Si no esta añadido a la lista
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    // Ocupa todo el ancho si esta desplegado
                                    .then(if (addShoppListUnfold) Modifier.fillMaxWidth() else Modifier)
                            ) {
                                if (addShoppListUnfold) {
                                    Text(
                                        "Añadir a la lista de la compra",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.AddShoppingCart,
                                    contentDescription = "Añadir",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        } else { // Si esta añadido a la lista
                            if (addShoppListUnfold) { // Si esta desplegado
                                if (quantityInShoppingList == 1) { // Si hay solo uno en la lista
                                    // Eliminar
                                    Icon(
                                        imageVector = Icons.Default.DeleteForever,
                                        contentDescription = "Eliminar",
                                        tint = Color.Red,
                                        modifier = Modifier
                                            .clickable { removeShoppingList(true) }
                                    )
                                } else { // Si hay mas de uno
                                    // Añadir
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clickable { removeShoppingList() }
                                    )
                                }

                                // shoppingList count
                                Text(
                                    quantityInShoppingList.toString(),
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                // Restar
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Añadir",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable { addShoppingList() }
                                )
                            } else { // Si está plegado
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ShoppingCart,
                                        contentDescription = "Añadir",
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    // shoppingList count
                                    Text(
                                        quantityInShoppingList.toString(),
                                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Check
                if (addShoppListUnfold && quantityInShoppingList > 0 && lastQuantityInShoppingList != -1) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 5.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
                            .shadow(6.dp, shape = RoundedCornerShape(10.dp))
                            .clickable { addShoppListUnfold = false }, // Lo pliega al confirmar
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirmar",
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Admin Section
            if (AuthManager.isAuthenticated()) {
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    // Titulo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Acciones",
                            tint = Color.Black
                        )
                        Text(
                            text = "Acciones",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .padding(start = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Opciones
                    Column {
                        // Destacar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp, bottom = 5.dp)
                                .shadow(4.dp, shape = RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .clickable { onFeature() }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (article!!.destacado) "Destacado" else "Destacar",
                                    style = MaterialTheme.typography.bodyLarge,
                                )

                                Checkbox(checked = article!!.destacado, onCheckedChange = {}, modifier = Modifier.size(20.dp))
                            }
                        }

                        // Ocultar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp, bottom = 5.dp)
                                .shadow(4.dp, shape = RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .clickable { onHide() }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (article!!.hidden) "Oculto" else "Ocultar",
                                    style = MaterialTheme.typography.bodyLarge,
                                )

                                Checkbox(checked = article!!.hidden, onCheckedChange = {}, modifier = Modifier.size(20.dp))
                            }
                        }

                        // Subir imagen
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp, bottom = 5.dp)
                                .shadow(4.dp, shape = RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .clickable { onUploadImage() }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Subir imagen",
                                    style = MaterialTheme.typography.bodyLarge,
                                )

                                if (uploadingImg) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 2.dp
                                    )
                                } else if (imgUploadSuccess) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Check",
                                        tint = Color.Green
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.FileUpload,
                                        contentDescription = "Subir imagen",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }


                }
            }
        }
    }
}