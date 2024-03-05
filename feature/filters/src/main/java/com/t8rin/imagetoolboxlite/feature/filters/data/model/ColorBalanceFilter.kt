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

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorBalanceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import com.t8rin.imagetoolboxlite.core.filters.domain.model.Filter


internal class ColorBalanceFilter(
    private val context: Context,
    override val value: FloatArray = floatArrayOf(
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f
    ),
) : GPUFilterTransformation(context), Filter.ColorBalance<Bitmap> {

    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageColorBalanceFilter().apply {
        setHighlights(value.take(3).toFloatArray())
        setMidtones(floatArrayOf(value[3], value[4], value[6]))
        setShowdows(value.takeLast(3).toFloatArray())
    }
}