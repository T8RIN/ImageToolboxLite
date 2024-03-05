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

package com.t8rin.imagetoolboxlite.core.filters.presentation.model

import android.graphics.Bitmap
import com.t8rin.imagetoolboxlite.core.filters.domain.model.Filter
import com.t8rin.imagetoolboxlite.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolboxlite.core.resources.R


class UiWhiteBalanceFilter(
    override val value: Pair<Float, Float> = 7000.0f to 100f,
) : UiFilter<Pair<Float, Float>>(
    title = R.string.white_balance,
    value = value,
    paramsInfo = listOf(
        FilterParam(R.string.temperature, 1000f..10000f, 0),
        FilterParam(R.string.tint, -100f..100f, 2)
    )
), Filter.WhiteBalance<Bitmap>