package com.alexmncn.flemingshop.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


class CodebarScannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(onScanResult = { result ->
                // val intent = Intent(this, ScanResultActivity::class.java)
                // intent.putExtra("scan_result", result)
                // startActivity(intent)
                Log.d("ScanResult", result)
            })
        }
    }
}

@Composable
fun MainScreen(onScanResult: (String) -> Unit) {
    // Estado para mostrar el resultado
    var scanResult by remember { mutableStateOf("") }

    // Configuración del lanzador de ZXing usando ScanContract
    val barcodeLauncher = rememberLauncherForActivityResult(contract = ScanContract()) { result ->
        if (result.contents != null) {
            scanResult = result.contents // Actualiza el resultado del escaneo
            onScanResult(scanResult)      // Llama a la función onScanResult para iniciar la actividad de resultado
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Catálogo de Artículos")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Configura las opciones del escáner y lánzalo
            val options = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                setPrompt("Escanea un código de barras")
                setCameraId(0) // Usa la cámara trasera
                setBeepEnabled(true)
                setOrientationLocked(false)
            }
            barcodeLauncher.launch(options)
        }) {
            Text(text = "Escanear Código de Barras")
        }
    }
}