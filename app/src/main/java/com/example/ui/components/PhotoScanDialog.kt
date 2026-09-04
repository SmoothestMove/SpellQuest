package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.CoralError
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.util.WordPhotoScanner
import kotlinx.coroutines.launch
import java.io.File

/**
 * Dialog enabling parents to snap a photo of a weekly spelling word list, worksheet,
 * or notebook page, and automatically extract and add the words to their spelling space.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoScanDialog(
    onDismiss: () -> Unit,
    onWordsAutoAdded: (List<String>) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    val extractedWords = remember { mutableStateListOf<String>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var manualAddInput by remember { mutableStateOf("") }
    var showManualAddRow by remember { mutableStateOf(false) }

    // Uri holder for full-resolution camera capture
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    fun processCapturedImage(bitmap: Bitmap) {
        capturedBitmap = bitmap
        isScanning = true
        errorMessage = null
        extractedWords.clear()

        scope.launch {
            try {
                val words = WordPhotoScanner.extractWordsFromBitmap(bitmap)
                isScanning = false
                if (words.isNotEmpty()) {
                    extractedWords.addAll(words)
                } else {
                    errorMessage = "No clear spelling words could be detected. Try taking a closer photo with good lighting or enter them manually."
                }
            } catch (e: Exception) {
                isScanning = false
                errorMessage = "Error scanning image: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    // Camera launcher for thumbnail / direct bitmap capture
    val cameraPreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            processCapturedImage(bitmap)
        }
    }

    // High-resolution camera capture via FileProvider
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempPhotoUri != null) {
            val bitmap = WordPhotoScanner.loadBitmapFromUri(context, tempPhotoUri!!)
            if (bitmap != null) {
                processCapturedImage(bitmap)
            } else {
                errorMessage = "Unable to process photo. Please try again."
            }
        }
    }

    // Gallery / photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = WordPhotoScanner.loadBitmapFromUri(context, uri)
            if (bitmap != null) {
                processCapturedImage(bitmap)
            } else {
                errorMessage = "Unable to load selected photo."
            }
        }
    }

    fun launchCamera() {
        try {
            val photoFile = File.createTempFile("spelling_list_", ".jpg", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, photoFile)
            tempPhotoUri = uri
            takePictureLauncher.launch(uri)
        } catch (_: Exception) {
            // Fallback to TakePicturePreview if FileProvider fails
            cameraPreviewLauncher.launch(null)
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isScanning) onDismiss()
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BentoPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        tint = BentoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Scan Spelling List",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = "Take a picture of words to auto-add",
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // STEP 1: If no photo has been taken yet
                if (capturedBitmap == null) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BentoSurfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📸",
                                fontSize = 42.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Snap a picture of your child's list",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Works with school homework sheets, handwritten study notes, or agenda word lists.",
                                fontSize = 12.sp,
                                color = BentoTextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Camera button (Primary)
                    Button(
                        onClick = { launchCamera() },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("snap_photo_camera_btn")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Picture with Camera", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Gallery button (Secondary)
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("pick_photo_gallery_btn")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp), tint = BentoPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose Photo from Gallery", fontSize = 13.sp, color = BentoPrimary)
                    }
                } else {
                    // STEP 2: Photo captured -> Showing preview & scan progress or results
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, BentoBorderSubtle, RoundedCornerShape(14.dp))
                    ) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Captured Word List",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Retake button overlay
                        if (!isScanning) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .clickable { launchCamera() }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Retake", tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Retake", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (isScanning) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = BentoPrimary,
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Analyzing photo and recognizing words...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = BentoTextPrimary
                            )
                            Text(
                                text = "Cleaning list numbers and isolating spelling words",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                    } else if (errorMessage != null) {
                        Surface(
                            color = CoralError.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "⚠️ $errorMessage",
                                    fontSize = 12.sp,
                                    color = CoralError,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { launchCamera() }) {
                                        Text("Try Again", color = BentoPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else if (extractedWords.isNotEmpty()) {
                        // Extracted words summary
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldSuccess.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "✓ Found ${extractedWords.size} spelling words",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                                Text(
                                    text = "Tap any chip to remove",
                                    fontSize = 11.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }

                        // Word chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            extractedWords.forEach { word ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = BentoSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                                    modifier = Modifier
                                        .testTag("scanned_word_chip_$word")
                                        .clickable { extractedWords.remove(word) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = word,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BentoTextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove $word",
                                            tint = BentoTextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Add word if one was missed
                        if (!showManualAddRow) {
                            TextButton(
                                onClick = { showManualAddRow = true },
                                modifier = Modifier.testTag("add_missed_word_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add a word missed by camera", fontSize = 12.sp)
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = manualAddInput,
                                    onValueChange = { manualAddInput = it },
                                    textStyle = highContrastInputTextStyle.copy(fontSize = 13.sp),
                                    placeholder = { Text("type missed word...", fontSize = 12.sp, color = BentoTextSecondary) },
                                    colors = highContrastTextFieldColors(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("manual_missed_word_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        val clean = manualAddInput.trim().lowercase()
                                        if (clean.isNotBlank() && !extractedWords.contains(clean)) {
                                            extractedWords.add(clean)
                                            manualAddInput = ""
                                            showManualAddRow = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = manualAddInput.isNotBlank()
                                ) {
                                    Text("Add", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (extractedWords.isNotEmpty()) {
                Button(
                    onClick = {
                        onWordsAutoAdded(extractedWords.toList())
                        Toast.makeText(context, "Added ${extractedWords.size} words to spelling list!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_auto_add_scanned_words_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Auto-Add ${extractedWords.size} Words", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isScanning
            ) {
                Text(if (extractedWords.isNotEmpty()) "Cancel" else "Close")
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}
