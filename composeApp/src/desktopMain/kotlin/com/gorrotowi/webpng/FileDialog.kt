package com.gorrotowi.webpng

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import java.awt.FileDialog

@Composable
fun FileDialog(
    onCloseRequest: (directory: String, fileName: String, extension: String) -> Unit
) = AwtWindow(
    create = {
        val fileDialog = object : FileDialog(null as java.awt.Frame?, "Choose a file", LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value && file != null) {
                    val fileNameSplit = file.split(".")
                    val fileName = fileNameSplit.first()
                    val fileExtension = if (fileNameSplit.size > 1) fileNameSplit.last() else ""
                    onCloseRequest(directory, fileName, fileExtension)
                }
            }
        }
        fileDialog.file = "*.jpg;*.jpeg"
        return@AwtWindow fileDialog
    },
    dispose = FileDialog::dispose
)