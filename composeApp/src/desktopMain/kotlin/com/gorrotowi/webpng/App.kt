package com.gorrotowi.webpng

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import webpng.composeapp.generated.resources.Res
import webpng.composeapp.generated.resources.arrow_right_bold
import webpng.composeapp.generated.resources.image_search_outline

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val coroutineScope = rememberCoroutineScope()
        val showFilePicker = remember { mutableStateOf(false) }
        val stroke = Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showFilePicker.value) {
                FileDialog { directory, fileNAme, extension ->
                    coroutineScope.launch {
                        convertImage(directory, fileNAme, extension)
                        showFilePicker.value = false
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Webp", style = MaterialTheme.typography.h4)
                Box {
                    Image(
                        painterResource(Res.drawable.arrow_right_bold), "",
                        modifier = Modifier.width(64.dp).height(64.dp),
                    )
                }
                Text("PNG", style = MaterialTheme.typography.h4)
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
                            painterResource(Res.drawable.image_search_outline), "Select image to convert",
                            modifier = Modifier.width(200.dp).height(200.dp),
                        )
                        Text(
                            "Select image to convert", style = MaterialTheme.typography.h4
                        )
                    }
                })
        }


    }
}