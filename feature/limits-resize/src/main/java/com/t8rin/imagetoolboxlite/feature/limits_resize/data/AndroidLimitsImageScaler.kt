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

package com.t8rin.imagetoolboxlite.feature.limits_resize.data

import android.graphics.Bitmap
import com.t8rin.imagetoolboxlite.core.domain.image.ImageScaler
import com.t8rin.imagetoolboxlite.core.domain.model.ResizeType
import com.t8rin.imagetoolboxlite.feature.limits_resize.domain.LimitsImageScaler
import com.t8rin.imagetoolboxlite.feature.limits_resize.domain.LimitsResizeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidLimitsImageScaler @Inject constructor(
    private val imageScaler: ImageScaler<Bitmap>
) : LimitsImageScaler<Bitmap>, ImageScaler<Bitmap> by imageScaler {

    override suspend fun scaleImage(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeType: LimitsResizeType
    ): Bitmap? = withContext(Dispatchers.IO) {
        val widthInternal = width.takeIf { it > 0 } ?: image.width
        val heightInternal = height.takeIf { it > 0 } ?: image.height

        resizeType.resizeWithLimits(
            image = image,
            width = widthInternal,
            height = heightInternal
        )
    }

    private val Bitmap.aspectRatio: Float get() = width / height.toFloat()

    private suspend fun LimitsResizeType.resizeWithLimits(
        image: Bitmap,
        width: Int,
        height: Int
    ): Bitmap? {
        val limitWidth: Int
        val limitHeight: Int

        if (autoRotateLimitBox && image.aspectRatio < 1f) {
            limitWidth = height
            limitHeight = width
        } else {
            limitWidth = width
            limitHeight = height
        }
        val limitAspectRatio = limitWidth / limitHeight.toFloat()

        if (image.height > limitHeight || image.width > limitWidth) {
            if (image.aspectRatio > limitAspectRatio) {
                return scaleImage(
                    image = image,
                    width = limitWidth,
                    height = limitWidth,
                    resizeType = ResizeType.Flexible
                )
            } else if (image.aspectRatio < limitAspectRatio) {
                return scaleImage(
                    image = image,
                    width = limitHeight,
                    height = limitHeight
                )
            } else {
                return scaleImage(
                    image = image,
                    width = limitWidth,
                    height = limitHeight
                )
            }
        } else {
            return when (this) {
                is LimitsResizeType.Recode -> image

                is LimitsResizeType.Zoom -> scaleImage(
                    image = image,
                    width = limitWidth,
                    height = limitHeight
                )

                is LimitsResizeType.Skip -> null
            }
        }
    }


}