@file:OptIn(ExperimentalComposeUiApi::class)

package com.gorrotowi.webpng.ui_components

import WeppyMascot
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.awt.datatransfer.DataFlavor
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileDropArea(onClicked: () -> Unit, onDroppedFiles: (files: List<File>) -> Unit) {
    var showTargetBorder by remember { mutableStateOf(false) }
    var fileList by remember { mutableStateOf<List<File>>(emptyList()) }

    val dragAndDropTarget = remember {
        object : DragAndDropTarget {

            // Highlights the border of a potential drop target
            override fun onStarted(event: DragAndDropEvent) {
                showTargetBorder = true
            }

            override fun onEnded(event: DragAndDropEvent) {
                showTargetBorder = false
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                fileList = event.awtTransferable.let {
                    if (it.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @Suppress("UNCHECKED_CAST")
                        (it.getTransferData(DataFlavor.javaFileListFlavor) as? List<File>)?.toList() ?: emptyList()
                    } else {
                        emptyList()
                    }
                }
                onDroppedFiles(fileList)
                return true
            }
        }
    }
    Row(
        Modifier
            .fillMaxSize()
            .then(
                if (showTargetBorder)
                    Modifier
                        .border(3.dp, Color.White, shape = RoundedCornerShape(16.dp))
                else
                    Modifier
                        .border(1.dp, Color.White, shape = RoundedCornerShape(16.dp))
            )
            .onClick { onClicked() }
            .dragAndDropTarget(
                shouldStartDragAndDrop = { true },
                target = dragAndDropTarget
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
        ) {
            WeppyMascot()
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Drop Here to Convert\nOr\nClick to Select Files", color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}