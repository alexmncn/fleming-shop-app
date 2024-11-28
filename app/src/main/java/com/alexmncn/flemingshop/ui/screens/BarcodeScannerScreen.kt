package com.alexmncn.flemingshop.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.components.ArticleCard
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BarcodeScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraPermissionState = remember { mutableStateOf(false) }

    // Gestor de permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionState.value = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Verificar permisos iniciales
    LaunchedEffect(Unit) {
        val cameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            cameraPermissionState.value = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    if (cameraPermissionState.value) {
        BarcodeScanner(navController = navController)
    } else {
        PermissionDeniedView(
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }
        )
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScanner(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val articleRepository: ArticleRepository by lazy { ArticleRepository(ApiService(apiClient)) }
    val scanDelay = 250L // Intervalo de tiempo en milisegundos en el que se escanea una nueva imagen

    var isFlashEnabled by remember { mutableStateOf(false) } // Linterna por defecto apagada
    var zoomLevel by remember { mutableFloatStateOf(0f) } // Zoom por defecto a 0
    var camera: Camera? by remember { mutableStateOf(null) } // Variable para almacenar la referencia de la cámara
    var statusMessage by remember { mutableStateOf("Escanea un código de barras") } // Mensaje de estado
    var scannedArticle by remember { mutableStateOf<Article?>(null) } // Artículo escaneado
    var articleVisible: Boolean by remember { mutableStateOf(false) } // Estado del articleCard (para controlar animacion)
    var lastCodebar: String by remember { mutableStateOf("") } // Ultimo codebar escaneado

    var isCameraInitialized by remember { mutableStateOf(false) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll() // Libera todos los casos de uso de la cámara al salir
            cameraProvider = null // Libera la referencia al proveedor de la cámara
        }
    }

    // Se llama cuando se escanea un nuevo código de barras
    fun onScan(scannedCodebar: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if ((scannedArticle != null) && (scannedCodebar != lastCodebar)) {
                articleVisible = false // Oculta el articulo para la animación
                delay(350) // Delay para que la animación se complete
                scannedArticle = null // Se limpia la variable del articulo
            }

            if (scannedCodebar != lastCodebar) {
                try {
                    statusMessage = "Cargando artículo..." // Mensaje de status
                    scannedArticle = articleRepository.getSearchArticles(search = scannedCodebar, filter = "codebar")[0]
                    articleVisible = true // Mostramos la articleCard cuando tenemos un nuevo articulo escaneado y cargado
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                    statusMessage = "Articulo desconocido" // Mensaje de status
                }
            }
            lastCodebar = scannedCodebar
        }
    }

    // Obtiene código de barras desde imagen
    fun getBarcodeFromImage(inputImage: InputImage, imageProxy: ImageProxy? = null, onScan: (String) -> Unit) {
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
                imageProxy?.close()
            }
    }

    // Extrae codigo de barras desde bitmap (para imagen de galería) usando la funcion principal
    fun processBarcodeFromBitmap(bitmap: Bitmap, onScan: (String) -> Unit) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        getBarcodeFromImage(inputImage, onScan = onScan)
    }

    // Seleccionar imagen de galería
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            processBarcodeFromBitmap(bitmap, onScan = { codebar -> onScan(codebar) })
        }
    }

    // Vista de la cámara
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
                // Controla la inicialización de la cámara (evita que se inicie más de una vez)
                if (!isCameraInitialized) {
                    val provider = cameraProviderFuture.get()
                    provider.unbindAll()
                    cameraProvider = provider

                    val analyzer = ImageAnalysis.Builder()
                        .build()
                        .also {
                            var lastScanTime = 0L
                            it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastScanTime >= scanDelay) {
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val inputImage = InputImage.fromMediaImage(
                                            mediaImage,
                                            imageProxy.imageInfo.rotationDegrees
                                        )
                                        getBarcodeFromImage(inputImage, imageProxy, onScan = { codebar -> onScan(codebar) })
                                    }
                                    lastScanTime = currentTime
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }

                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    camera = provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        analyzer
                    )
                    isCameraInitialized = true
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        },
        modifier = Modifier
            .fillMaxSize(),
        update = {
            camera?.let {
                it.cameraControl.enableTorch(isFlashEnabled)
                it.cameraControl.setLinearZoom(zoomLevel)
            }
        }
    )


    // Interfaz sobre la cámara

    // Contenedor mitad superior
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Fondo semitransparente con recorte del rectángulo indicador
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val rectPadding = 25.dp.toPx()
                val rectWidth = size.width - rectPadding * 2
                val rectHeight = size.height - rectPadding * 2

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Fondo negro semitransparente
                    drawRect(
                        color = Color.Black.copy(alpha = 0.3f)
                    )

                    // Área recortada transparente
                    drawRect(
                        color = Color.Transparent,
                        topLeft = Offset(rectPadding, rectPadding),
                        size = Size(rectWidth, rectHeight),
                        blendMode = BlendMode.Clear
                    )
                }

                // Borde blanco alrededor del área transparente
                drawRect(
                    color = Color.White,
                    topLeft = Offset(rectPadding, rectPadding),
                    size = Size(rectWidth, rectHeight),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // Contenedor mitad inferior
        Box(
            modifier = Modifier
                .weight(1f) // Ocupa la otra mitad del espacio disponible
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            // Fondo negro semitransparente
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawRect(
                    color = Color.Black.copy(alpha = 0.3f)
                )
            }

            Column {
                // Animación al mostrar el ArticleCard
                AnimatedVisibility(
                    visible = articleVisible, // Se muestra solo si hay un artículo escaneado
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(), // Animación de entrada
                    exit = fadeOut(), // Animación de salida
                    content = {
                        scannedArticle?.let { article ->
                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                            ) {
                                ArticleCard(
                                    article = article,
                                    navController = navController
                                )
                            }
                        }
                    }
                )
            }

            // Status message
            if (scannedArticle == null) {
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                )
            }
        }
    }

    // Botones alineados en el borde inferior izquierdo
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .fillMaxSize()
            .padding(end = 16.dp, bottom = 16.dp) // Margen desde la esquina inferior izquierda
    ) {
        IconButton(
            onClick = { isFlashEnabled = !isFlashEnabled }, // Cambia el estado del flash al pulsar
            modifier = Modifier
                .background(if (isFlashEnabled) Color.White else MaterialTheme.colorScheme.primary , CircleShape)
        ) {
            Icon(
                imageVector = if (isFlashEnabled) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                contentDescription = if (isFlashEnabled) "Apagar Linterna" else "Encender Linterna",
                tint = if (isFlashEnabled) MaterialTheme.colorScheme.primary else Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(
            onClick = { pickImageLauncher.launch("image/*") },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.ImageSearch,
                contentDescription = "Seleccionar imagen",
                tint = Color.White
            )
        }
    }
}

@Composable
fun PermissionDeniedView(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Se necesita permiso de cámara para escanear códigos de barras.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("Conceder Permiso")
        }
    }
}