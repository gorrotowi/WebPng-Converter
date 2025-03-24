package com.gorrotowi.webpng

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path

suspend fun convertImage(
    directoryPath: String,
    fileName: String,
    extension: String
): Path? {
    val fullPath = "$directoryPath$fileName.$extension"
    return withContext(Dispatchers.IO) {
        val image = ImmutableImage.loader()
            .fromFile(fullPath)
        val path = directoryPath.replace("~", System.getProperty("user.home"))
        image.output(PngWriter.NoCompression, "$path$fileName.png")
    }
}