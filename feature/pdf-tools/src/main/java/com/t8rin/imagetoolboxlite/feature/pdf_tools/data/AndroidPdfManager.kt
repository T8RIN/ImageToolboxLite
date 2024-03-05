/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolboxlite.feature.pdf_tools.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import com.t8rin.imagetoolboxlite.core.domain.image.ImageScaler
import com.t8rin.imagetoolboxlite.core.domain.model.IntegerSize
import com.t8rin.imagetoolboxlite.core.domain.model.Preset
import com.t8rin.imagetoolboxlite.feature.pdf_tools.domain.PdfManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt


internal class AndroidPdfManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    private val imageScaler: ImageScaler<Bitmap>
) : PdfManager<Bitmap> {

    override suspend fun convertImagesToPdf(
        imageUris: List<String>,
        onProgressChange: suspend (Int) -> Unit,
        scaleSmallImagesToLarge: Boolean
    ): ByteArray = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()

        val (size, images) = calculateCombinedImageDimensionsAndBitmaps(
            imageUris = imageUris,
            scaleSmallImagesToLarge = scaleSmallImagesToLarge,
            isHorizontal = false,
            imageSpacing = 0
        )

        val bitmaps = images.map { image ->
            if (scaleSmallImagesToLarge && image.shouldUpscale(false, size)) {
                image.upscale(false, size)
            } else image
        }

        bitmaps.forEachIndexed { index, imageBitmap ->
            val pageInfo = PdfDocument.PageInfo.Builder(
                imageBitmap.width,
                imageBitmap.height,
                index
            ).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawBitmap(
                imageBitmap,
                0f, 0f,
                Paint().apply {
                    isAntiAlias = true
                }
            )
            pdfDocument.finishPage(page)
            delay(10L)
            onProgressChange(index)
        }

        val out = ByteArrayOutputStream()
        pdfDocument.writeTo(out)

        return@withContext out.toByteArray().also {
            out.flush()
            out.close()
        }
    }

    override fun convertPdfToImages(
        pdfUri: String,
        pages: List<Int>?,
        preset: Preset.Numeric,
        onGetPagesCount: suspend (Int) -> Unit,
        onProgressChange: suspend (Int, Bitmap) -> Unit,
        onComplete: suspend () -> Unit
    ) = CoroutineScope(Dispatchers.Main).launch {
        withContext(Dispatchers.IO) {
            context.contentResolver.openFileDescriptor(
                pdfUri.toUri(),
                "r"
            )?.use { fileDescriptor ->
                val pdfRenderer = PdfRenderer(fileDescriptor)

                onGetPagesCount(pages?.size ?: pdfRenderer.pageCount)

                for (pageIndex in 0 until pdfRenderer.pageCount) {
                    if (pages == null || pages.contains(pageIndex)) {
                        val bitmap: Bitmap
                        pdfRenderer.openPage(pageIndex).use { page ->
                            bitmap = imageScaler.scaleUntilCanShow(
                                Bitmap.createBitmap(
                                    (page.width * (preset.value / 100f)).roundToInt(),
                                    (page.height * (preset.value / 100f)).roundToInt(),
                                    Bitmap.Config.ARGB_8888
                                )
                            )!!
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                        }

                        val renderedBitmap = Bitmap.createBitmap(
                            bitmap.width,
                            bitmap.height,
                            getSuitableConfig(bitmap)
                        )
                        Canvas(renderedBitmap).apply {
                            drawColor(Color.White.toArgb())
                            drawBitmap(bitmap, 0f, 0f, Paint().apply { isAntiAlias = true })
                        }

                        onProgressChange(pageIndex, renderedBitmap)
                    }
                }
                onComplete()
                pdfRenderer.close()
            }
        }
    }

    override suspend fun getPdfPages(uri: String): List<Int> {
        return withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openFileDescriptor(
                    uri.toUri(),
                    "r"
                )?.use { fileDescriptor ->
                    List(PdfRenderer(fileDescriptor).pageCount) { it }
                }
            }.getOrNull() ?: emptyList()
        }
    }

    private val pagesBuf = hashMapOf<String, List<IntegerSize>>()
    override suspend fun getPdfPageSizes(uri: String): List<IntegerSize> {
        return withContext(Dispatchers.IO) {
            if (!pagesBuf[uri].isNullOrEmpty()) {
                pagesBuf[uri]!!
            } else {
                runCatching {
                    context.contentResolver.openFileDescriptor(
                        uri.toUri(),
                        "r"
                    )?.use { fileDescriptor ->
                        val r = PdfRenderer(fileDescriptor)
                        List(r.pageCount) {
                            val page = r.openPage(it)
                            page?.run {
                                IntegerSize(width, height)
                            }.also {
                                page.close()
                            }
                        }.filterNotNull().also {
                            pagesBuf[uri] = it
                        }
                    }
                }.getOrNull() ?: emptyList()
            }
        }
    }

    private suspend fun calculateCombinedImageDimensionsAndBitmaps(
        imageUris: List<String>,
        isHorizontal: Boolean,
        scaleSmallImagesToLarge: Boolean,
        imageSpacing: Int,
    ): Pair<IntegerSize, List<Bitmap>> = withContext(Dispatchers.IO) {
        var w = 0
        var h = 0
        var maxHeight = 0
        var maxWidth = 0
        val drawables = imageUris.mapNotNull { uri ->
            imageLoader.execute(
                ImageRequest
                    .Builder(context)
                    .data(uri)
                    .size(Size.ORIGINAL)
                    .build()
            ).drawable?.toBitmap()?.apply {
                maxWidth = max(maxWidth, width)
                maxHeight = max(maxHeight, height)
            }
        }

        drawables.forEachIndexed { index, image ->
            val width = image.width
            val height = image.height

            val spacing = if (index != drawables.lastIndex) imageSpacing else 0

            if (scaleSmallImagesToLarge && image.shouldUpscale(
                    isHorizontal = isHorizontal,
                    size = IntegerSize(maxWidth, maxHeight)
                )
            ) {
                val targetHeight: Int
                val targetWidth: Int

                if (isHorizontal) {
                    targetHeight = maxHeight
                    targetWidth = (targetHeight * image.aspectRatio).toInt()
                } else {
                    targetWidth = maxWidth
                    targetHeight = (targetWidth / image.aspectRatio).toInt()
                }
                if (isHorizontal) {
                    w += (targetWidth + spacing).coerceAtLeast(1)
                } else {
                    h += (targetHeight + spacing).coerceAtLeast(1)
                }
            } else {
                if (isHorizontal) {
                    w += (width + spacing).coerceAtLeast(1)
                } else {
                    h += (height + spacing).coerceAtLeast(1)
                }
            }
        }

        if (isHorizontal) {
            h = maxHeight
        } else {
            w = maxWidth
        }

        IntegerSize(w, h) to drawables
    }

    private fun Drawable.toBitmap(): Bitmap {
        val drawable = this
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                getSuitableConfig()
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                getSuitableConfig()
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getSuitableConfig(
        image: Bitmap? = null
    ): Bitmap.Config = image?.config ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Bitmap.Config.RGBA_1010102
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Bitmap.Config.RGBA_F16
    } else {
        Bitmap.Config.ARGB_8888
    }

    private val Bitmap.aspectRatio: Float get() = width / height.toFloat()

    private fun Bitmap.shouldUpscale(
        isHorizontal: Boolean,
        size: IntegerSize
    ): Boolean {
        return if (isHorizontal) this.height != size.height
        else this.width != size.width
    }

    private suspend fun Bitmap.upscale(
        isHorizontal: Boolean,
        size: IntegerSize
    ): Bitmap {
        return if (isHorizontal) {
            createScaledBitmap(
                width = (size.height * aspectRatio).toInt(),
                height = size.height
            )
        } else {
            createScaledBitmap(
                width = size.width,
                height = (size.width / aspectRatio).toInt()
            )
        }
    }

    private suspend fun Bitmap.createScaledBitmap(
        width: Int,
        height: Int
    ): Bitmap = imageScaler.scaleImage(
        image = this,
        width = width,
        height = height
    )

}