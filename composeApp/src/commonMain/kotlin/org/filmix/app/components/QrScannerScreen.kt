package org.filmix.app.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun QrScannerScreen(modifier: Modifier, onQrCodeScanned: (String) -> Unit)