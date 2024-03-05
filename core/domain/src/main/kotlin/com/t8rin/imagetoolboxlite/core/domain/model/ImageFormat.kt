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

import com.t8rin.imagetoolboxlite.core.domain.Domain

sealed class ImageFormat(
    val title: String,
    val extension: String,
    val type: String,
    val canChangeCompressionValue: Boolean,
    val canWriteExif: Boolean = false,
    val compressionTypes: List<CompressionType> = listOf(CompressionType.Quality(0..100))
) : Domain {
    data object PngLossless : ImageFormat(
        title = "PNG Lossless",
        extension = "png",
        type = "image/png",
        canChangeCompressionValue = false,
        canWriteExif = true
    )

    data object Jpg : ImageFormat(
        title = "JPG",
        extension = "jpg",
        type = "image/jpeg",
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    data object Jpeg : ImageFormat(
        title = "JPEG",
        extension = "jpeg",
        type = "image/jpeg",
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    sealed class Webp(
        title: String,
        compressionTypes: List<CompressionType>
    ) : ImageFormat(
        extension = "webp",
        type = "image/webp",
        canChangeCompressionValue = true,
        title = title,
        canWriteExif = true,
        compressionTypes = compressionTypes
    ) {
        data object Lossless : Webp(
            title = "WEBP Lossless",
            compressionTypes = listOf(CompressionType.Effort(0..100))
        )

        data object Lossy : Webp(
            title = "WEBP Lossy",
            compressionTypes = listOf(CompressionType.Quality(0..100))
        )
    }

    data object Bmp : ImageFormat(
        title = "BMP",
        extension = "bmp",
        type = "image/bmp",
        canChangeCompressionValue = false
    )

    companion object {
        sealed class CompressionType(
            open val compressionRange: IntRange = 0..100
        ) {
            data class Quality(
                override val compressionRange: IntRange = 0..100
            ) : CompressionType(compressionRange)

            data class Effort(
                override val compressionRange: IntRange = 0..100
            ) : CompressionType(compressionRange)
        }

        fun Default(): ImageFormat = Jpg

        operator fun get(typeString: String?): ImageFormat = when {
            typeString == null -> Default()
            typeString.contains("png") -> PngLossless
            typeString.contains("bmp") -> Bmp
            typeString.contains("jpeg") -> Jpeg
            typeString.contains("jpg") -> Jpg
            typeString.contains("webp") -> Webp.Lossless
            else -> Default()
        }

        val alphaContainedEntries: List<ImageFormat>
            get() = listOf(
                PngLossless,
                Webp.Lossy,
                Webp.Lossless
            )

        val highLevelFormats: List<ImageFormat>
            get() = listOf()

        val entries
            get() = listOf(
                Jpg,
                Jpeg,
                PngLossless,
                Bmp,
                Webp.Lossy,
                Webp.Lossless
            )
    }
}