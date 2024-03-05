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

package com.t8rin.imagetoolboxlite.core.domain.model

import android.graphics.Bitmap
import com.t8rin.imagetoolboxlite.core.domain.Domain

data class ImageInfo(
    val width: Int = 0,
    val height: Int = 0,
    val quality: Quality = Quality.Base(),
    val imageFormat: ImageFormat = ImageFormat.Default(),
    val resizeType: ResizeType = ResizeType.Explicit,
    val rotationDegrees: Float = 0f,
    val isFlipped: Boolean = false,
    val sizeInBytes: Int = 0,
    val originalUri: String? = null
) : Domain {
    fun haveChanges(original: Bitmap?): Boolean {
        if (original == null) return false
        return quality.qualityValue != 100 || rotationDegrees != 0f || isFlipped || width != original.width || height != original.height
    }
}