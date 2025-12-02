package com.gorrotowi.webpng

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.webp.WebpWriter
import java.io.File
import java.nio.file.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class ImageFormat {
    WEBP,
    PNG
}

suspend fun convertImage(
        toFormat: ImageFormat = ImageFormat.WEBP,
        file: File,
        destinationPath: String
): Path? {

    println("Converting ${file.path} to $toFormat")
    return withContext(Dispatchers.IO) {
        val image = ImmutableImage.loader().fromFile(file)
        val path =
                if (destinationPath.endsWith(File.separator)) destinationPath
                else "$destinationPath${File.separator}"
        val fileName = file.nameWithoutExtension
        when (toFormat) {
            ImageFormat.WEBP -> image.output(WebpWriter(), "$path$fileName.webp")
            ImageFormat.PNG -> image.output(PngWriter.NoCompression, "$path$fileName.png")
        }
    }
}
