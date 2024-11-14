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
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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


class CodebarScannerActivity : AppCompatActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false

        if (cameraPermissionGranted) {
            // Si los permisos son concedidos, inicia la cámara
            setContent {
                BarcodeScannerScreen(onScanResult = { result ->
                    Log.d("ScanResult", result)
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                })
            }
        } else {
            Toast.makeText(this, "Permisos requeridos no concedidos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        // Verificar permisos antes de iniciar la actividad
        checkPermissions()
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
                BarcodeScannerScreen(onScanResult = { result ->
                    Log.d("ScanResult", result)
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                })
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerScreen(
    onScanResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlashEnabled by remember { mutableStateOf(false) } // Linterna por defecto apagada
    var zoomLevel by remember { mutableFloatStateOf(0f) } // Zoom por defecto a 0

    val lifecycleOwner = LocalLifecycleOwner.current
    var camera: Camera? by remember { mutableStateOf(null) } // Variable para almacenar la referencia de la cámara

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
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            barcode.rawValue?.let { value ->
                                                onScanResult(value)
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
                Log.d("UI", "Linterna: $isFlashEnabled, Zoom: $zoomLevel")
            }
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = {
            isFlashEnabled = !isFlashEnabled
            Log.d("UI", "Linterna cambiada a: $isFlashEnabled")
        }) {
            Text(text = if (isFlashEnabled) "Apagar Linterna" else "Encender Linterna")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = zoomLevel,
            onValueChange = { newZoom ->
                zoomLevel = newZoom
                Log.d("UI", "Zoom cambiado a: $zoomLevel")
            },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
