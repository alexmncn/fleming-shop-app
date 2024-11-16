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
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.ui.graphics.Color
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.components.ArticleCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodebarScannerActivity : AppCompatActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false

        if (cameraPermissionGranted) {
            // Si los permisos son concedidos, inicia la cámara
            setContent {
                BarcodeScannerScreen()
            }
        } else {
            Toast.makeText(this, "Permisos requeridos no concedidos", Toast.LENGTH_SHORT).show()
        }
    }

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
                BarcodeScannerScreen()
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

    var isFlashEnabled by remember { mutableStateOf(false) } // Linterna por defecto apagada
    var zoomLevel by remember { mutableFloatStateOf(0f) } // Zoom por defecto a 0
    var camera: Camera? by remember { mutableStateOf(null) } // Variable para almacenar la referencia de la cámara
    var scannedArticle by remember { mutableStateOf<Article?>(null) } // Articulo escaneado

    fun onScan(codebar: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Guarda la primera coincidencia de la lista de articles (solo deberia haber uno)
                scannedArticle = articleRepository.getSearchArticles(search = codebar, filter = "codebar")[0]
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
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
                val barcodeScanner = BarcodeScanning.getClient()
                val analyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                barcodeScanner.process(inputImage)
                                    .addOnSuccessListener { barcodes -> // Cuando detecta el codebar ejecuta la funcion onScan
                                        for (barcode in barcodes) {
                                            barcode.rawValue?.let { value ->
                                                onScan(value) // Salida
                                            }
                                        }
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
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
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Mostrar el ArticleCard si hay un artículo escaneado
        scannedArticle?.let { article ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
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
            modifier = Modifier
                .padding(8.dp)
                .background(Color.Blue, shape = CircleShape)
        ) {
            Icon(
                imageVector = if (isFlashEnabled) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                contentDescription = if (isFlashEnabled) "Apagar Linterna" else "Encender Linterna",
                tint = androidx.compose.ui.graphics.Color.White
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = zoomLevel,
            onValueChange = { newZoom ->
                zoomLevel = newZoom
            },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}