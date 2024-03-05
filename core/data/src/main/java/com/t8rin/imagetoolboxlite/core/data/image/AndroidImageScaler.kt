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

package com.t8rin.imagetoolboxlite.core.data.image

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.applyCanvas
import com.t8rin.imagetoolboxlite.core.domain.image.ImageScaler
import com.t8rin.imagetoolboxlite.core.domain.image.ImageTransformer
import com.t8rin.imagetoolboxlite.core.domain.model.IntegerSize
import com.t8rin.imagetoolboxlite.core.domain.model.ResizeType
import com.t8rin.imagetoolboxlite.core.filters.domain.FilterProvider
import com.t8rin.imagetoolboxlite.core.filters.domain.model.Filter
import com.t8rin.imagetoolboxlite.core.settings.domain.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

internal class AndroidImageScaler @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>
) : ImageScaler<Bitmap> {

    override suspend fun scaleImage(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeType: ResizeType
    ): Bitmap = withContext(Dispatchers.IO) {

        val widthInternal = width.takeIf { it > 0 } ?: image.width
        val heightInternal = height.takeIf { it > 0 } ?: image.height

        return@withContext when (resizeType) {
            ResizeType.Explicit -> {
                createScaledBitmap(
                    image,
                    width = widthInternal,
                    height = heightInternal
                )
            }

            ResizeType.Flexible -> {
                flexibleResize(
                    image = image,
                    max = max(widthInternal, heightInternal)
                )
            }

            is ResizeType.CenterCrop -> {
                resizeType.resizeWithCenterCrop(
                    image = image,
                    targetWidth = widthInternal,
                    targetHeight = heightInternal,
                    scaleFactor = resizeType.scaleFactor
                )
            }
        }
    }

    override suspend fun scaleUntilCanShow(
        image: Bitmap?
    ): Bitmap? = withContext(Dispatchers.IO) {
        if (image == null) return@withContext null

        var (height, width) = image.run { height to width }

        var iterations = 0
        while (!canShow(size = height * width * 4)) {
            height = (height * 0.85f).roundToInt()
            width = (width * 0.85f).roundToInt()
            iterations++
        }

        return@withContext if (iterations == 0) image
        else scaleImage(
            image = image,
            height = height,
            width = width
        )
    }

    private fun canShow(size: Int): Boolean {
        return size < 3096 * 3096 * 3
    }

    private suspend fun ResizeType.CenterCrop.resizeWithCenterCrop(
        image: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        scaleFactor: Float
    ): Bitmap {
        val mTargetWidth = (targetWidth / scaleFactor).roundToInt()
        val mTargetHeight = (targetHeight / scaleFactor).roundToInt()

        val originalSize = if (!originalSize.isDefined()) {
            IntegerSize(
                (image.width * scaleFactor).roundToInt(),
                (image.height * scaleFactor).roundToInt()
            )
        } else originalSize

        if (mTargetWidth == originalSize.width && mTargetHeight == originalSize.height) return image
        val bitmap = imageTransformer.transform(
            image = image.let { bitmap ->
                val xScale: Float = mTargetWidth.toFloat() / originalSize.width
                val yScale: Float = mTargetHeight.toFloat() / originalSize.height
                val scale = xScale.coerceAtLeast(yScale)
                createScaledBitmap(
                    image = bitmap,
                    width = (scale * originalSize.width).toInt(),
                    height = (scale * originalSize.height).toInt()
                )
            },
            transformations = listOf(
                filterProvider.filterToTransformation(
                    object : Filter.StackBlur<Bitmap> {
                        override val value: Pair<Float, Int>
                            get() = 0.5f to blurRadius
                    }
                )
            )
        )

        val drawImage = createScaledBitmap(
            image = image,
            width = (originalSize.width * scaleFactor).roundToInt(),
            height = (originalSize.height * scaleFactor).roundToInt()
        )

        return Bitmap.createBitmap(
            mTargetWidth,
            mTargetHeight,
            drawImage.config
        ).apply {
            setHasAlpha(true)
        }.applyCanvas {
            drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
            this@resizeWithCenterCrop.canvasColor?.let {
                drawColor(it)
            } ?: bitmap?.let {
                drawBitmap(
                    bitmap,
                    (width - bitmap.width) / 2f,
                    (height - bitmap.height) / 2f,
                    null
                )
            }
            val left = (width - drawImage.width) / 2f
            val top = (height - drawImage.height) / 2f
            drawBitmap(
                drawImage,
                null,
                RectF(
                    left,
                    top,
                    drawImage.width + left,
                    drawImage.height + top
                ),
                null
            )
        }
    }

    private suspend fun createScaledBitmap(
        image: Bitmap,
        width: Int,
        height: Int
    ): Bitmap {
        if (width == image.width && height == image.height) return image

        return BitmapCompat.createScaledBitmap(image, width, height, null, true)
    }

    private suspend fun flexibleResize(
        image: Bitmap,
        max: Int
    ): Bitmap {
        return kotlin.runCatching {
            if (image.height >= image.width) {
                val aspectRatio = image.width.toDouble() / image.height.toDouble()
                val targetWidth = (max * aspectRatio).toInt()
                createScaledBitmap(image, targetWidth, max)
            } else {
                val aspectRatio = image.height.toDouble() / image.width.toDouble()
                val targetHeight = (max * aspectRatio).toInt()
                createScaledBitmap(image, max, targetHeight)
            }
        }.getOrNull() ?: image
    }

}