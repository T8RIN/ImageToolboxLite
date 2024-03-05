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

package com.t8rin.imagetoolboxlite.core.domain.saving.model

import com.t8rin.imagetoolboxlite.core.domain.model.ImageFormat
import com.t8rin.imagetoolboxlite.core.domain.saving.SaveTarget

data class FileSaveTarget(
    override val originalUri: String,
    override val filename: String,
    override val imageFormat: ImageFormat,
    override val data: ByteArray,
) : SaveTarget {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileSaveTarget

        if (originalUri != other.originalUri) return false
        if (filename != other.filename) return false
        if (imageFormat != other.imageFormat) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = originalUri.hashCode()
        result = 31 * result + filename.hashCode()
        result = 31 * result + imageFormat.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}