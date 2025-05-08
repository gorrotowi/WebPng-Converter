@file:OptIn(ExperimentalFoundationApi::class)

package com.gorrotowi.webpng

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.gorrotowi.webpng.ui_components.FileDropArea
import com.gorrotowi.webpng.ui_components.RadioGroup
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import webpng.composeapp.generated.resources.Res
import webpng.composeapp.generated.resources.arrow_right_bold
import webpng.composeapp.generated.resources.image_search_outline
import java.awt.FileDialog
import java.awt.Frame

@Composable
fun App() {
    WebpToPngContent()
}

fun selectDirectory(): String? {
    val fileDialog = FileDialog(null as Frame?, "Select Directory", FileDialog.LOAD)
    System.setProperty("apple.awt.fileDialogForDirectories", "true")
    fileDialog.isVisible = true

    val selectedDirectory = fileDialog.directory
    System.setProperty("apple.awt.fileDialogForDirectories", "false") // Reset the property

    return selectedDirectory
}

@Composable
fun WebpToPngContent() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Wepb to PNG", "Image to Webp")

    val coroutineScope = rememberCoroutineScope()
    val showFilePicker = remember { mutableStateOf(false) }

    if (showFilePicker.value) {
        val directory = selectDirectory()
        println("Selected directory: $directory")
        showFilePicker.value = false
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        RadioGroup(options) { index, text ->
            println("Selected $text")
            selectedIndex = index
        }

        Text(
            "Saved path: ", color = Color.LightGray,
            modifier = Modifier
                .clickable {
                    showFilePicker.value = true
                })

        Spacer(modifier = Modifier.height(16.dp))

        FileDropArea(onClicked = {
//            showFilePicker.value = true
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

@Composable
fun AppContent() {
    val coroutineScope = rememberCoroutineScope()
    val showFilePicker = remember { mutableStateOf(false) }
    val stroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showFilePicker.value) {
//            FileDialog { directory, fileNAme, extension ->
//                coroutineScope.launch {
////                    convertImage(ImageFormat.PNG, directory, fileNAme, extension)
//                    showFilePicker.value = false
//                }
//            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Webp", style = MaterialTheme.typography.h4, color = Color.White)
            Box {
                Image(
                    painterResource(Res.drawable.arrow_right_bold), "",
                    modifier = Modifier.width(64.dp).height(64.dp),
                )
            }
            Text("PNG", style = MaterialTheme.typography.h4)
        }


        FileDropArea(onClicked = {

        }) {
            println("Dropped files: $it")
        }
        TooltipArea(
            tooltip = {
                Text("Select image to convert")
            },
            content = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            showFilePicker.value = true
                        }
                        .drawBehind {
                            drawRoundRect(
                                color = Color.Black,
                                style = stroke,
                                cornerRadius = CornerRadius(16.dp.toPx())
                            )
                        }
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(Color(0xFFccd9de))
                        .padding(16.dp)
                ) {
                    Image(
                        painterResource(Res.drawable.image_search_outline),
                        contentDescription = "Select image to convert",
                        modifier = Modifier.width(200.dp).height(200.dp),
                    )
                    Text(
                        "Select image to convert", style = MaterialTheme.typography.h4
                    )
                }
            })
    }
}