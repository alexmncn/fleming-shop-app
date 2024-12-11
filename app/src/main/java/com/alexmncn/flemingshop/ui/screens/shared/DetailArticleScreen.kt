package com.alexmncn.flemingshop.ui.screens.shared

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.network.AuthManager
import com.alexmncn.flemingshop.data.repository.AdminActionsRepository
import com.alexmncn.flemingshop.data.repository.CatalogRepository
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

@Composable
fun DetailArticleScreen(codebar: String, navController: NavController) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val catalogRepository: CatalogRepository by lazy { CatalogRepository(ApiService(apiClient)) }
    val adminActionsRepository: AdminActionsRepository by lazy { AdminActionsRepository(ApiService(apiClient)) }

    var article by remember { mutableStateOf<Article?>(null) }
    val imageUrl = Constans.IMAGES_URL + "articles/${article?.codebar}.webp"
    var tags by remember { mutableIntStateOf(0) }
    var uploadingImg by remember { mutableStateOf(false) }
    var imgUploadSuccess by remember { mutableStateOf(false) }

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
                        article =
                            article!!.copy(destacado = false) // Actualiza el estado local del parametro
                        checkTags(article) // Actualiza las
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


    article?.let {
        Column (
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen del artículo",
                modifier = Modifier
                    .fillMaxWidth(),
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
                        .align(Alignment.End)
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
                        .align(Alignment.End)
                ) {
                    // Etiqueta de Stock
                    StockLabel(article!!.stock, modifier = Modifier.shadow(2.dp, shape = RoundedCornerShape(20.dp)))
                }
            }

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