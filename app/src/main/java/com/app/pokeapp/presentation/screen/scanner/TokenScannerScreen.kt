package com.app.pokeapp.presentation.screen.scanner

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.app.pokeapp.domain.model.Pokemon
import com.app.pokeapp.domain.model.ScanMode
import com.app.pokeapp.presentation.theme.PokemonColors
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import kotlin.math.min

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TokenScannerScreen(
    scanMode: ScanMode,
    onResult: (List<Pokemon>) -> Unit,
    onDismiss: () -> Unit,
    viewModel: TokenScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        viewModel.setScanMode(scanMode)
    }

    // Auto-return when confirmed
    LaunchedEffect(uiState.isConfirmed) {
        if (uiState.isConfirmed && uiState.detectedPokemon.isNotEmpty()) {
            delay(1000) // brief pause to show result
            onResult(uiState.detectedPokemon)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            !cameraPermissionState.status.isGranted -> {
                CameraPermissionRequest(
                    shouldShowRationale = cameraPermissionState.status.shouldShowRationale,
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                    onDismiss = onDismiss
                )
            }
            uiState.isInitializing -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Preparazione scanner...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                CameraScannerContent(
                    scanMode = scanMode,
                    uiState = uiState,
                    viewModel = viewModel,
                    onDismiss = onDismiss
                )
            }
        }

        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Chiudi",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun CameraPermissionRequest(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (shouldShowRationale)
                "La fotocamera serve per scansionare i gettoni Pok\u00e9mon"
            else
                "Permesso fotocamera necessario per la scansione",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(containerColor = PokemonColors.Primary)
        ) {
            Text("Concedi permesso")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onDismiss) {
            Text("Annulla", color = Color.White)
        }
    }
}

@Composable
private fun CameraScannerContent(
    scanMode: ScanMode,
    uiState: TokenScannerUiState,
    viewModel: TokenScannerViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview with continuous analysis
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { previewView ->
                    previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(
                                    ContextCompat.getMainExecutor(ctx)
                                ) { imageProxy ->
                                    viewModel.processFrame(imageProxy)
                                }
                            }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis
                            )
                        } catch (_: Exception) {}
                    }, ContextCompat.getMainExecutor(ctx))
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        DisposableEffect(Unit) {
            onDispose {
                try {
                    ProcessCameraProvider.getInstance(context).get().unbindAll()
                } catch (_: Exception) {}
            }
        }

        // Viewfinder overlay
        ScannerOverlay(scanMode = scanMode, isConfirmed = uiState.isConfirmed)

        // Instruction text
        val instructionText = when {
            uiState.isConfirmed -> "Pok\u00e9mon trovato!"
            uiState.detectedPokemon.isNotEmpty() -> "Riconoscimento in corso..."
            else -> when (scanMode) {
                is ScanMode.SingleToken -> "Inquadra il nome sul gettone"
                is ScanMode.DualToken -> "Inquadra i nomi sui due gettoni"
            }
        }

        Text(
            text = instructionText,
            color = if (uiState.isConfirmed) PokemonColors.Success else Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(12.dp)
        )

        // Bottom panel with detected Pokemon
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.detectedPokemon.isNotEmpty()) {
                uiState.detectedPokemon.forEachIndexed { index, pokemon ->
                    val label = if (scanMode is ScanMode.DualToken) {
                        if (index == 0) "Giocatore" else "Avversario"
                    } else null

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (label != null) {
                            Text(
                                text = "$label: ",
                                color = if (index == 0) PokemonColors.Success else PokemonColors.Error,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Text(
                            text = pokemon.nameItalian,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (uiState.isConfirmed) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Confermato!",
                        color = PokemonColors.Success,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                // Scanning animation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "Scansione in corso...",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ScannerOverlay(scanMode: ScanMode, isConfirmed: Boolean) {
    val accentColor = if (isConfirmed) PokemonColors.Success else Color.White

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 3.dp.toPx()
        val cornerRadius = CornerRadius(16.dp.toPx())

        when (scanMode) {
            is ScanMode.SingleToken -> {
                val viewfinderSize = min(size.width, size.height) * 0.6f
                val left = (size.width - viewfinderSize) / 2
                val top = (size.height - viewfinderSize) / 2 - 40.dp.toPx()

                drawRoundRect(
                    color = accentColor,
                    topLeft = Offset(left, top),
                    size = Size(viewfinderSize, viewfinderSize),
                    cornerRadius = cornerRadius,
                    style = Stroke(strokeWidth)
                )

                val accentLength = viewfinderSize * 0.12f
                val accentWidth = 5.dp.toPx()
                drawCornerAccents(left, top, viewfinderSize, viewfinderSize, accentLength, accentWidth, accentColor)
            }
            is ScanMode.DualToken -> {
                val regionSize = min(size.width, size.height) * 0.38f
                val spacing = 16.dp.toPx()
                val totalWidth = regionSize * 2 + spacing
                val startX = (size.width - totalWidth) / 2
                val y = (size.height - regionSize) / 2 - 40.dp.toPx()

                val leftColor = if (isConfirmed) PokemonColors.Success else PokemonColors.Success
                val rightColor = if (isConfirmed) PokemonColors.Success else PokemonColors.Error

                drawRoundRect(
                    color = leftColor,
                    topLeft = Offset(startX, y),
                    size = Size(regionSize, regionSize),
                    cornerRadius = cornerRadius,
                    style = Stroke(strokeWidth)
                )
                drawCornerAccents(startX, y, regionSize, regionSize, regionSize * 0.12f, 5.dp.toPx(), leftColor)

                drawRoundRect(
                    color = rightColor,
                    topLeft = Offset(startX + regionSize + spacing, y),
                    size = Size(regionSize, regionSize),
                    cornerRadius = cornerRadius,
                    style = Stroke(strokeWidth)
                )
                drawCornerAccents(startX + regionSize + spacing, y, regionSize, regionSize, regionSize * 0.12f, 5.dp.toPx(), rightColor)
            }
        }
    }

    if (scanMode is ScanMode.DualToken) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Giocatore",
                    color = PokemonColors.Success,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "Avversario",
                    color = PokemonColors.Error,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCornerAccents(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    accentLength: Float,
    accentWidth: Float,
    color: Color
) {
    val cap = androidx.compose.ui.graphics.StrokeCap.Round
    drawLine(color, Offset(left, top + accentLength), Offset(left, top), accentWidth, cap = cap)
    drawLine(color, Offset(left, top), Offset(left + accentLength, top), accentWidth, cap = cap)
    drawLine(color, Offset(left + width, top), Offset(left + width - accentLength, top), accentWidth, cap = cap)
    drawLine(color, Offset(left + width, top), Offset(left + width, top + accentLength), accentWidth, cap = cap)
    drawLine(color, Offset(left, top + height), Offset(left + accentLength, top + height), accentWidth, cap = cap)
    drawLine(color, Offset(left, top + height), Offset(left, top + height - accentLength), accentWidth, cap = cap)
    drawLine(color, Offset(left + width, top + height), Offset(left + width - accentLength, top + height), accentWidth, cap = cap)
    drawLine(color, Offset(left + width, top + height), Offset(left + width, top + height - accentLength), accentWidth, cap = cap)
}
