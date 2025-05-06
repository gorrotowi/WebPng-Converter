@file:OptIn(ExperimentalComposeUiApi::class)

package com.gorrotowi.webpng.ui_components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.awt.datatransfer.DataFlavor
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileDropArea(onDroppedFiles :(files: List<File>) -> Unit) {
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
    Box(
        Modifier
            .size(200.dp)
            .background(Color.LightGray)
            .then(
                if (showTargetBorder)
                    Modifier.border(BorderStroke(3.dp, Color.Black))
                else
                    Modifier
            )
            .dragAndDropTarget(
                shouldStartDragAndDrop = { true },
                target = dragAndDropTarget
            )
    ) {
        Text("Drop Here", Modifier.align(Alignment.Center))
    }
}