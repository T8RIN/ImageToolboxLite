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

package com.t8rin.imagetoolboxlite.feature.image_stitch.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.t8rin.imagetoolboxlite.core.domain.image.ImageGetter
import com.t8rin.imagetoolboxlite.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolboxlite.core.domain.image.ImageScaler
import com.t8rin.imagetoolboxlite.core.domain.image.ImageTransformer
import com.t8rin.imagetoolboxlite.core.domain.image.ShareProvider
import com.t8rin.imagetoolboxlite.core.domain.model.ImageFormat
import com.t8rin.imagetoolboxlite.core.domain.model.ImageInfo
import com.t8rin.imagetoolboxlite.core.domain.model.ImageWithSize
import com.t8rin.imagetoolboxlite.core.domain.model.IntegerSize
import com.t8rin.imagetoolboxlite.core.domain.model.Quality
import com.t8rin.imagetoolboxlite.core.domain.model.withSize
import com.t8rin.imagetoolboxlite.core.filters.domain.FilterProvider
import com.t8rin.imagetoolboxlite.core.filters.domain.model.FadeSide
import com.t8rin.imagetoolboxlite.core.filters.domain.model.Filter
import com.t8rin.imagetoolboxlite.core.filters.domain.model.SideFadeParams
import com.t8rin.imagetoolboxlite.feature.image_stitch.domain.CombiningParams
import com.t8rin.imagetoolboxlite.feature.image_stitch.domain.ImageCombiner
import com.t8rin.imagetoolboxlite.feature.image_stitch.domain.StitchMode
import java.util.UUID
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlin.math.max

internal class AndroidImageCombiner @Inject constructor(
    private val imageScaler: ImageScaler<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>
) : ImageCombiner<Bitmap> {

    override suspend fun combineImages(
        imageUris: List<String>,
        combiningParams: CombiningParams,
        imageScale: Float
    ): Pair<Bitmap, ImageInfo> = withContext(Dispatchers.IO) {
        suspend fun getImageData(
            imagesUris: List<String>,
            isHorizontal: Boolean
        ): Pair<Bitmap, ImageInfo> {
            val (size, images) = calculateCombinedImageDimensionsAndBitmaps(
                imageUris = imagesUris,
                isHorizontal = isHorizontal,
                scaleSmallImagesToLarge = combiningParams.scaleSmallImagesToLarge,
                imageSpacing = combiningParams.spacing
            )

            val bitmaps = images.map { image ->
                if (combiningParams.scaleSmallImagesToLarge && image.shouldUpscale(
                        isHorizontal,
                        size
                    )
                ) {
                    image.upscale(isHorizontal, size)
                } else image
            }

            val bitmap = Bitmap.createBitmap(size.width, size.height, getSuitableConfig())
            val canvas = Canvas(bitmap).apply {
                drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
                drawColor(combiningParams.backgroundColor)
            }

            var pos = 0
            for (i in imagesUris.indices) {
                var bmp = bitmaps[i]

                combiningParams.spacing.takeIf { it < 0 && combiningParams.fadingEdgesMode != null }
                    ?.let {
                        val space = combiningParams.spacing.absoluteValue
                        val bottomFilter = object : Filter.SideFade<Bitmap> {
                            override val value: SideFadeParams
                                get() = SideFadeParams.Absolute(
                                    side = if (isHorizontal) {
                                        FadeSide.End
                                    } else FadeSide.Bottom,
                                    size = space
                                )
                        }
                        val topFilter = object : Filter.SideFade<Bitmap> {
                            override val value: SideFadeParams
                                get() = SideFadeParams.Absolute(
                                    side = if (isHorizontal) {
                                        FadeSide.Start
                                    } else FadeSide.Top,
                                    size = space
                                )
                        }
                        val filters = if (combiningParams.fadingEdgesMode == 0) {
                            when (i) {
                                0 -> listOf()
                                else -> listOf(topFilter)
                            }
                        } else {
                            when (i) {
                                0 -> listOf(bottomFilter)
                                imagesUris.lastIndex -> listOf(topFilter)
                                else -> listOf(topFilter, bottomFilter)
                            }
                        }.map {
                            filterProvider.filterToTransformation(it)
                        }

                        imageTransformer.transform(bmp, filters)?.let { bmp = it }
                    }

                if (isHorizontal) {
                    canvas.drawBitmap(
                        bmp,
                        pos.toFloat(),
                        0f,
                        null
                    )
                } else {
                    canvas.drawBitmap(
                        bmp,
                        0f,
                        pos.toFloat(),
                        null
                    )
                }
                pos += if (isHorizontal) {
                    (bmp.width + combiningParams.spacing).coerceAtLeast(1)
                } else (bmp.height + combiningParams.spacing).coerceAtLeast(1)
            }

            return imageScaler.scaleImage(
                image = bitmap,
                width = (size.width * imageScale).toInt(),
                height = (size.height * imageScale).toInt()
            ) to ImageInfo(
                width = (size.width * imageScale).toInt(),
                height = (size.height * imageScale).toInt()
            )
        }

        if (combiningParams.stitchMode.gridCellsCount().let { !(it == 0 || it > imageUris.size) }) {
            combineImages(
                imageUris = distributeImages(
                    images = imageUris,
                    cellCount = combiningParams.stitchMode.gridCellsCount()
                ).mapNotNull { images ->
                    val data = getImageData(
                        imagesUris = images,
                        isHorizontal = combiningParams.stitchMode.isHorizontal()
                    )
                    shareProvider.cacheImage(
                        image = data.first,
                        imageInfo = data.second,
                        name = UUID.randomUUID().toString()
                    )
                },
                combiningParams = combiningParams.copy(
                    stitchMode = when (combiningParams.stitchMode) {
                        is StitchMode.Grid.Horizontal -> StitchMode.Vertical
                        else -> StitchMode.Horizontal
                    }
                ),
                imageScale = imageScale
            )
        } else getImageData(
            imagesUris = imageUris,
            isHorizontal = combiningParams.stitchMode.isHorizontal()
        )
    }

    override suspend fun calculateCombinedImageDimensions(
        imageUris: List<String>,
        combiningParams: CombiningParams
    ): IntegerSize {
        return if (combiningParams.stitchMode.gridCellsCount()
                .let { it == 0 || it > imageUris.size }
        ) {
            calculateCombinedImageDimensionsAndBitmaps(
                imageUris = imageUris,
                isHorizontal = combiningParams.stitchMode.isHorizontal(),
                scaleSmallImagesToLarge = combiningParams.scaleSmallImagesToLarge,
                imageSpacing = combiningParams.spacing
            ).first
        } else {
            val isHorizontalGrid = combiningParams.stitchMode.isHorizontal()
            var size = IntegerSize(0, 0)
            distributeImages(
                images = imageUris,
                cellCount = combiningParams.stitchMode.gridCellsCount()
            ).forEach { images ->
                calculateCombinedImageDimensionsAndBitmaps(
                    imageUris = images,
                    isHorizontal = !isHorizontalGrid,
                    scaleSmallImagesToLarge = combiningParams.scaleSmallImagesToLarge,
                    imageSpacing = combiningParams.spacing
                ).first.let { newSize ->
                    size = if (isHorizontalGrid) {
                        size.copy(
                            height = size.height + newSize.height,
                            width = max(newSize.width, size.width)
                        )
                    } else {
                        size.copy(
                            height = max(newSize.height, size.height),
                            width = size.width + newSize.width,
                        )
                    }
                }
            }
            size
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
            imageGetter.getImage(
                data = uri,
                originalSize = true
            )?.apply {
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

    private fun distributeImages(
        images: List<String>,
        cellCount: Int
    ): List<List<String>> {
        val imageCount = images.size
        val imagesPerRow = imageCount / cellCount
        val remainingImages = imageCount % cellCount

        val result = MutableList(cellCount) { imagesPerRow }

        for (i in 0 until remainingImages) {
            result[i] += 1
        }

        var offset = 0
        return result.map { count ->
            images.subList(
                fromIndex = offset,
                toIndex = offset + count
            ).also {
                offset += count
            }
        }
    }

    override suspend fun createCombinedImagesPreview(
        imageUris: List<String>,
        combiningParams: CombiningParams,
        imageFormat: ImageFormat,
        quality: Quality,
        onGetByteCount: (Int) -> Unit
    ): ImageWithSize<Bitmap> = withContext(Dispatchers.IO) {
        val imageSize = calculateCombinedImageDimensions(
            imageUris = imageUris,
            combiningParams = combiningParams
        )

        combineImages(
            imageUris = imageUris,
            combiningParams = combiningParams,
            imageScale = 1f
        ).let { (image, imageInfo) ->
            return@let imagePreviewCreator.createPreview(
                image = image,
                imageInfo = imageInfo.copy(
                    imageFormat = imageFormat,
                    quality = quality
                ),
                transformations = emptyList(),
                onGetByteCount = onGetByteCount
            ) withSize imageSize
        }
    }

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
            imageScaler.scaleImage(
                image = this,
                width = (size.height * aspectRatio).toInt(),
                height = size.height
            )
        } else {
            imageScaler.scaleImage(
                image = this,
                width = size.width,
                height = (size.width / aspectRatio).toInt()
            )
        }
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

}