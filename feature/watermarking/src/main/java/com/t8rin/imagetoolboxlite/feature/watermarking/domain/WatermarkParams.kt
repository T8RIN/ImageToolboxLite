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

package com.t8rin.imagetoolboxlite.feature.watermarking.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri

data class WatermarkParams(
    val positionX: Float,
    val positionY: Float,
    val rotation: Int,
    val alpha: Float,
    val isRepeated: Boolean,
    val overlayMode: Int,
    val watermarkingType: WatermarkingType
) {
    companion object {
        val Default by lazy {
            WatermarkParams(
                positionX = 0f,
                positionY = 0f,
                rotation = 45,
                alpha = 0.5f,
                isRepeated = true,
                overlayMode = 3,
                watermarkingType = WatermarkingType.Text.Default
            )
        }
    }
}

sealed interface WatermarkingType {
    data class Text(
        val color: Int,
        val style: Int,
        val size: Float,
        val font: Int,
        val backgroundColor: Int,
        val text: String,
    ) : WatermarkingType {
        companion object {
            val Default by lazy {
                Text(
                    color = Color.Black.toArgb(),
                    style = 0,
                    size = 10f,
                    font = 0,
                    backgroundColor = Color.Transparent.toArgb(),
                    text = "Watermark"
                )
            }
        }
    }

    data class Image(
        val imageData: Any,
        val size: Float
    ) : WatermarkingType {
        companion object {
            val Default by lazy {
                Image(
                    size = 0.1f,
                    imageData = "file:///android_asset/svg/emotions/aasparkles.svg".toUri()
                )
            }
        }
    }

    companion object {
        val entries by lazy {
            listOf(Text.Default, Image.Default)
        }
    }
}