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

package com.t8rin.imagetoolboxlite.feature.image_stitch.domain

import com.t8rin.imagetoolboxlite.core.domain.model.ImageFormat
import com.t8rin.imagetoolboxlite.core.domain.model.ImageInfo
import com.t8rin.imagetoolboxlite.core.domain.model.ImageWithSize
import com.t8rin.imagetoolboxlite.core.domain.model.IntegerSize
import com.t8rin.imagetoolboxlite.core.domain.model.Quality

interface ImageCombiner<I> {

    suspend fun combineImages(
        imageUris: List<String>,
        combiningParams: CombiningParams,
        imageScale: Float
    ): Pair<I, ImageInfo>

    suspend fun calculateCombinedImageDimensions(
        imageUris: List<String>,
        combiningParams: CombiningParams
    ): IntegerSize

    suspend fun createCombinedImagesPreview(
        imageUris: List<String>,
        combiningParams: CombiningParams,
        imageFormat: ImageFormat,
        quality: Quality,
        onGetByteCount: (Int) -> Unit
    ): ImageWithSize<I>

}