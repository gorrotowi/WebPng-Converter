@file:OptIn(ExperimentalFoundationApi::class)

package com.gorrotowi.webpng

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gorrotowi.webpng.ui_components.FileDropArea
import com.gorrotowi.webpng.ui_components.RadioGroup
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.pictureDir
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.File

@Composable
fun App() {
    FileKit.init("WebPng")
    WebpToPngContent()
}

@Composable
fun WebpToPngContent() {

    val cachePath = with(FileKit.pictureDir.path) {
        if (isNullOrEmpty()) System.getProperty("user.home") else this
    }

    val pathToSaveFiles = remember { mutableStateOf(cachePath) }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("PNG", "Webp")

    val coroutineScope = rememberCoroutineScope()
    val directorySelectorLauncher =
        rememberDirectoryPickerLauncher(title = "Select directory to save files") { directory ->
            directory?.path?.let { pathToSaveFiles.value = it }
        }

    val selectFilesLauncher = rememberFilePickerLauncher(
        title = "Select files to convert",
        type = FileKitType.Image,
        mode = FileKitMode.Multiple()
    ) { files ->
        coroutineScope.launch {
            files?.map { file ->
                async {
                    val format = when (selectedIndex) {
                        0 -> ImageFormat.PNG
                        1 -> ImageFormat.WEBP
                        else -> ImageFormat.PNG
                    }
                    convertImage(format, file.file)
                }
            }?.awaitAll()
            Desktop.getDesktop().open(File(pathToSaveFiles.value))
        }

    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    directorySelectorLauncher.launch()
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black, contentColor = Color.White),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text("Select Directory")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = pathToSaveFiles.value, color = Color.LightGray,
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Convert Image to:", color = Color.LightGray)

        RadioGroup(options) { index, text ->
            println("Selected $text")
            selectedIndex = index
        }

        FileDropArea(onClicked = {
            selectFilesLauncher.launch()
        }) { fileList ->
            println("Dropped files: $fileList")
            if (fileList.isNotEmpty()) {
                coroutineScope.launch {
                    fileList.map { file ->
                        async {
                            when (selectedIndex) {
                                0 -> convertImage(
                                    ImageFormat.PNG,
                                    file,
                                )

                                1 -> convertImage(
                                    ImageFormat.WEBP,
                                    file,
                                )

                                else -> null
                            }
                        }
                    }.awaitAll()
                }
            }
        }
    }
}