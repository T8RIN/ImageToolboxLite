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

package com.t8rin.imagetoolboxlite.feature.filters.data.model

import android.graphics.Bitmap
import com.t8rin.imagetoolboxlite.core.domain.image.Transformation
import com.t8rin.imagetoolboxlite.core.domain.model.IntegerSize
import com.t8rin.imagetoolboxlite.core.filters.domain.model.Filter
import com.t8rin.imagetoolboxlite.feature.filters.data.utils.Pixelate
import com.t8rin.imagetoolboxlite.feature.filters.data.utils.PixelationLayer


internal class PixelationFilter(
    override val value: Float = 25f,
) : Filter.Pixelation<Bitmap>, Transformation<Bitmap> {
    override val cacheKey: String
        get() = (value).hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        return Pixelate.fromBitmap(
            input = input,
            layers = arrayOf(
                PixelationLayer.Builder(PixelationLayer.Shape.Square)
                    .setResolution(value - 4f)
                    .setSize(value)
                    .build()
            )
        )
    }
}