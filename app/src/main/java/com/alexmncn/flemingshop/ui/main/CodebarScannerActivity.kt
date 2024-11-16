package com.alexmncn.flemingshop.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources.Theme
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.components.ArticleCard
import com.alexmncn.flemingshop.ui.components.MainTopBar
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme
import com.google.android.material.color.utilities.Scheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CodebarScannerActivity : AppCompatActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false

        if (cameraPermissionGranted) {
            // Si los permisos son concedidos, inicia la cámara
            setContent {
                FlemingShopTheme {
                    Scaffold (
                        topBar = { MainTopBar() },
                        content = {
                            BarcodeScannerScreen()
                        }
                    )
                }
            }
        } else {
            Toast.makeText(this, "Permisos requeridos no concedidos", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    private fun checkPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos si no están concedidos
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA)
            )
        } else {
            // Si ya están concedidos, inicia la cámara
            setContent {
                FlemingShopTheme {
                    Scaffold (
                        topBar = { MainTopBar() },
                        content = { BarcodeScannerScreen() }
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        // Verificar permisos antes de iniciar la actividad
        checkPermissions()
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerScreen(
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val articleRepository: ArticleRepository by lazy { ArticleRepository(ApiService) }
    val scanDelay = 250L // Intervalo de tiempo en milisegundos en el que se escanea una nueva imagen

    var isFlashEnabled by remember { mutableStateOf(false) } // Linterna por defecto apagada
    var zoomLevel by remember { mutableFloatStateOf(0f) } // Zoom por defecto a 0
    var camera: Camera? by remember { mutableStateOf(null) } // Variable para almacenar la referencia de la cámara
    var scannedArticle by remember { mutableStateOf<Article?>(null) } // Artículo escaneado

    fun onScan(codebar: String) {
        Log.d("codebar", codebar)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Guarda la primera coincidencia de la lista de articles (solo deberia haber uno)
                scannedArticle = articleRepository.getSearchArticles(search = codebar, filter = "codebar")[0]
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
        }
    }

    // Contexto actual para la funcionalidad de cargar imágenes
    val context = LocalContext.current
    // Evento para eleccionar imágenes desde la galería
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Procesar la imagen seleccionada y luego llama a onScan
            processBarcodeFromBitmap(bitmap, onScan = { codebar -> onScan(codebar) })
        }
    }

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val analyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        var lastScanTime = 0L // Tiempo del último escaneo

                        it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                            val currentTime = System.currentTimeMillis()

                            // Procesa la imagen si ha pasado el intervalo deseado (ejemplo: 500 ms)
                            if (currentTime - lastScanTime >= scanDelay) {
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                    // Llama a la funcion para obtener el codebar y luego llamar a onScan
                                    getBarcodeFromImage(inputImage, imageProxy, onScan = { codebar -> onScan(codebar)})
                                }
                                lastScanTime = currentTime // Actualiza el tiempo del último análisis
                            } else {
                                imageProxy.close() // Cierra la imagen si no es tiempo de analizar
                            }
                        }
                    }


                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                // Guardar la referencia de la cámara después de enlazar el flujo de trabajo
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    analyzer
                )

            }, ContextCompat.getMainExecutor(context))

            previewView
        },
        modifier = modifier
            .fillMaxSize(),
        update = { previewView -> // En un AndroidView se debe utilizar esta seccion para reflejar los cambios de la UI
            // Usar la referencia de la cámara almacenada en la variable `camera` para actulizar la configuración
            camera?.let {
                it.cameraControl.enableTorch(isFlashEnabled)
                it.cameraControl.setLinearZoom(zoomLevel)
            }
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Mostrar el ArticleCard si hay un artículo escaneado
        scannedArticle?.let { article ->
            Box(
                modifier = modifier
                    .align(Alignment.Center)
                    .width(200.dp)
            ) {
                ArticleCard(article = article)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(
            onClick = { isFlashEnabled = !isFlashEnabled }, // Cambia el estado del flash al pulsar
            modifier = modifier
                .padding(8.dp)
                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
        ) {
            Icon(
                imageVector = if (isFlashEnabled) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                contentDescription = if (isFlashEnabled) "Apagar Linterna" else "Encender Linterna",
                tint = androidx.compose.ui.graphics.Color.White
            )
        }
        Spacer(modifier = modifier.height(8.dp))

        IconButton(
            onClick = { pickImageLauncher.launch("image/*") },
            modifier = modifier
                .padding(8.dp)
                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.ImageSearch,
                contentDescription = "Seleccionar imagen",
                tint = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}

// Función para procesar un Bitmap de una imagen cargada desde la galería y detectar códigos de barras
private fun processBarcodeFromBitmap(bitmap: Bitmap, onScan: (String) -> Unit) {
    val inputImage = InputImage.fromBitmap(bitmap, 0)
    getBarcodeFromImage(inputImage, onScan = onScan)
}

// Función para detectar codigos de barrras de una imagen
private fun getBarcodeFromImage(inputImage: InputImage, imageProxy: ImageProxy? = null, onScan: (String) -> Unit) {
    val barcodeScanner = BarcodeScanning.getClient()
    barcodeScanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
                barcode.rawValue?.let { value ->
                    onScan(value)
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.e("BarcodeScanner", "Error al procesar la imagen: ${exception.message}")
        }
        .addOnCompleteListener {
            imageProxy?.close() // Libera el recurso
        }
}