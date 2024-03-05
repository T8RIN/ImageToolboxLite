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

package com.t8rin.imagetoolboxlite.feature.filters.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolboxlite.core.filters.domain.model.FadeSide
import com.t8rin.imagetoolboxlite.core.filters.domain.model.FilterValueWrapper
import com.t8rin.imagetoolboxlite.core.filters.domain.model.GlitchParams
import com.t8rin.imagetoolboxlite.core.filters.domain.model.NEAREST_ODD_ROUNDING
import com.t8rin.imagetoolboxlite.core.filters.domain.model.SideFadeParams
import com.t8rin.imagetoolboxlite.core.filters.domain.model.wrap
import com.t8rin.imagetoolboxlite.core.filters.presentation.model.UiColorFilter
import com.t8rin.imagetoolboxlite.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolboxlite.core.filters.presentation.model.UiRGBFilter
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedIconButton
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.ToggleGroupButton
import com.t8rin.imagetoolboxlite.core.ui.widget.color_picker.ColorSelectionRow
import com.t8rin.imagetoolboxlite.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolboxlite.core.ui.widget.controls.EnhancedSlider
import com.t8rin.imagetoolboxlite.core.ui.widget.controls.EnhancedSliderItem
import com.t8rin.imagetoolboxlite.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolboxlite.core.ui.widget.text.RoundedTextField
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
internal fun <T> FilterItemContent(
    filter: UiFilter<T>,
    onFilterChange: (value: Any) -> Unit,
    modifier: Modifier = Modifier,
    previewOnly: Boolean = false
) {
    Column(
        modifier = modifier
    ) {
        when (val value = filter.value) {
            is FilterValueWrapper<*> -> {
                when (val wrapped = (filter.value as FilterValueWrapper<*>).wrapped) {
                    is Color -> {
                        Box(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            ColorSelectionRow(
                                value = wrapped,
                                defaultColors = remember(filter) {
                                    derivedStateOf {
                                        ColorSelectionRowDefaults.colorList.map {
                                            if (filter is UiColorFilter) it.copy(0.5f)
                                            else it
                                        }
                                    }
                                }.value,
                                allowAlpha = filter !is UiRGBFilter,
                                allowScroll = !previewOnly,
                                onValueChange = {
                                    onFilterChange(it.wrap())
                                }
                            )
                        }
                    }
                }
            }

            is FloatArray -> {
                val rows = filter.paramsInfo[0].valueRange.start.toInt().absoluteValue
                var text by rememberSaveable(value) {
                    mutableStateOf(
                        value.let {
                            var string = ""
                            it.forEachIndexed { index, float ->
                                string += "$float, "
                                if (index % rows == (rows - 1)) string += "\n"
                            }
                            string.dropLast(3)
                        }
                    )
                }
                RoundedTextField(
                    enabled = !previewOnly,
                    modifier = Modifier.padding(16.dp),
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { text = it },
                    onLoseFocusTransformation = {
                        val matrix = filter.newInstance().value as FloatArray
                        this.trim { it.isWhitespace() }.split(",").mapIndexed { index, num ->
                            num.toFloatOrNull()?.let {
                                matrix[index] = it
                            }
                        }
                        onFilterChange(matrix)
                        this
                    },
                    endIcon = {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = {
                                val matrix = filter.newInstance().value as FloatArray
                                text.trim { it.isWhitespace() }.split(",")
                                    .mapIndexed { index, num ->
                                        num.toFloatOrNull()?.let {
                                            matrix[index] = it
                                        }
                                    }
                                onFilterChange(matrix)
                            }
                        ) {
                            Icon(Icons.Rounded.Done, null)
                        }
                    },
                    value = text,
                    label = { Text(stringResource(R.string.float_array_of)) }
                )
            }

            is Float -> {
                EnhancedSlider(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                        .offset(y = (-2).dp),
                    enabled = !previewOnly,
                    value = value,
                    onValueChange = {
                        onFilterChange(it.roundTo(filter.paramsInfo.first().roundTo))
                    },
                    valueRange = filter.paramsInfo.first().valueRange
                )
            }

            is Pair<*, *> -> {
                when {
                    value.first is Number && value.second is Number -> {
                        val sliderState1: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
                        val sliderState2: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }

                        LaunchedEffect(
                            sliderState1.value,
                            sliderState2.value
                        ) {
                            onFilterChange(
                                sliderState1.value to sliderState2.value
                            )
                        }

                        val paramsInfo by remember(filter) {
                            derivedStateOf {
                                filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                                    if (filterParam.title == null) return@mapIndexedNotNull null
                                    when (index) {
                                        0 -> sliderState1
                                        else -> sliderState2
                                    } to filterParam
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            paramsInfo.forEach { (state, info) ->
                                val (title, valueRange, roundTo) = info
                                EnhancedSliderItem(
                                    enabled = !previewOnly,
                                    value = state.value,
                                    title = stringResource(title!!),
                                    valueRange = valueRange,
                                    onValueChange = {
                                        state.value = it
                                    },
                                    internalStateTransformation = {
                                        it.roundTo(roundTo)
                                    },
                                    behaveAsContainer = false
                                )
                            }
                        }
                    }

                    value.first is Color && value.second is Color -> {
                        Box(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            var color1 by remember(value) { mutableStateOf(value.first as Color) }
                            var color2 by remember(value) { mutableStateOf(value.second as Color) }

                            Column {
                                Text(
                                    text = stringResource(R.string.first_color),
                                    modifier = Modifier
                                        .padding(
                                            bottom = 16.dp,
                                            top = 16.dp,
                                            end = 16.dp,
                                        )
                                )
                                ColorSelectionRow(
                                    value = color1,
                                    allowScroll = !previewOnly,
                                    onValueChange = { color ->
                                        color1 = color
                                        onFilterChange(color1 to color2)
                                    }
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.second_color),
                                    modifier = Modifier
                                        .padding(
                                            top = 16.dp,
                                            bottom = 16.dp,
                                            end = 16.dp
                                        )
                                )
                                ColorSelectionRow(
                                    value = color2,
                                    allowScroll = !previewOnly,
                                    onValueChange = { color ->
                                        color2 = color
                                        onFilterChange(color1 to color2)
                                    }
                                )
                            }
                        }
                    }

                    value.first is Float && value.second is Color -> {
                        var sliderState1 by remember { mutableFloatStateOf((value.first as Number).toFloat()) }
                        var color1 by remember(value) { mutableStateOf(value.second as Color) }

                        EnhancedSliderItem(
                            modifier = Modifier
                                .padding(
                                    top = 8.dp,
                                    start = 8.dp,
                                    end = 8.dp
                                ),
                            enabled = !previewOnly,
                            value = sliderState1,
                            title = filter.paramsInfo[0].title?.let {
                                stringResource(it)
                            } ?: "",
                            onValueChange = {
                                sliderState1 = it
                                onFilterChange(sliderState1 to color1)
                            },
                            internalStateTransformation = {
                                it.roundTo(filter.paramsInfo[0].roundTo)
                            },
                            valueRange = filter.paramsInfo[0].valueRange,
                            behaveAsContainer = false
                        )
                        Box(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            Column {
                                Text(
                                    text = stringResource(filter.paramsInfo[1].title!!),
                                    modifier = Modifier
                                        .padding(
                                            bottom = 16.dp,
                                            top = 16.dp,
                                            end = 16.dp,
                                        )
                                )
                                ColorSelectionRow(
                                    value = color1,
                                    allowScroll = !previewOnly,
                                    allowAlpha = true,
                                    onValueChange = { color ->
                                        color1 = color
                                        onFilterChange(sliderState1 to color1)
                                    }
                                )
                            }
                        }
                    }

                    value.first is Number && value.second is Boolean -> {
                        var sliderState1 by remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
                        var booleanState2 by remember(value) { mutableStateOf(value.second as Boolean) }

                        EnhancedSliderItem(
                            modifier = Modifier
                                .padding(
                                    top = 8.dp,
                                    start = 8.dp,
                                    end = 8.dp
                                ),
                            enabled = !previewOnly,
                            value = sliderState1,
                            title = filter.paramsInfo[0].title?.let {
                                stringResource(it)
                            } ?: "",
                            onValueChange = {
                                sliderState1 = it
                                onFilterChange(sliderState1 to booleanState2)
                            },
                            internalStateTransformation = {
                                it.roundTo(filter.paramsInfo[0].roundTo)
                            },
                            valueRange = filter.paramsInfo[0].valueRange,
                            behaveAsContainer = false
                        )
                        filter.paramsInfo[1].takeIf { it.title != null }
                            ?.let { (title, _, _) ->
                                PreferenceRowSwitch(
                                    title = stringResource(id = title!!),
                                    checked = booleanState2,
                                    onClick = {
                                        booleanState2 = it
                                        onFilterChange(sliderState1 to it)
                                    },
                                    modifier = Modifier.padding(
                                        top = 16.dp,
                                        start = 12.dp,
                                        end = 12.dp,
                                        bottom = 12.dp
                                    ),
                                    applyHorPadding = false,
                                    startContent = {},
                                    resultModifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                                )
                            }
                    }
                }
            }

            is Triple<*, *, *> -> {
                when {
                    value.first is Number && value.second is Number && value.third is Number -> {
                        val sliderState1: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
                        val sliderState2: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }
                        val sliderState3: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.third as Number).toFloat()) }

                        LaunchedEffect(
                            sliderState1.value,
                            sliderState2.value,
                            sliderState3.value
                        ) {
                            onFilterChange(
                                Triple(
                                    sliderState1.value,
                                    sliderState2.value,
                                    sliderState3.value
                                )
                            )
                        }

                        val paramsInfo by remember(filter) {
                            derivedStateOf {
                                filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                                    if (filterParam.title == null) return@mapIndexedNotNull null
                                    when (index) {
                                        0 -> sliderState1
                                        1 -> sliderState2
                                        else -> sliderState3
                                    } to filterParam
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            paramsInfo.forEach { (state, info) ->
                                val (title, valueRange, roundTo) = info
                                EnhancedSliderItem(
                                    enabled = !previewOnly,
                                    value = state.value,
                                    title = stringResource(title!!),
                                    valueRange = valueRange,
                                    onValueChange = {
                                        state.value = it
                                    },
                                    internalStateTransformation = {
                                        it.roundTo(roundTo)
                                    },
                                    behaveAsContainer = false
                                )
                            }
                        }
                    }

                    value.first is Number && value.second is Number && value.third is Color -> {
                        val sliderState1: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
                        val sliderState2: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }
                        var color3 by remember(value) { mutableStateOf(value.third as Color) }

                        LaunchedEffect(
                            sliderState1.value,
                            sliderState2.value,
                            color3
                        ) {
                            onFilterChange(
                                Triple(sliderState1.value, sliderState2.value, color3)
                            )
                        }

                        val paramsInfo by remember(filter) {
                            derivedStateOf {
                                filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                                    if (filterParam.title == null || index > 1) return@mapIndexedNotNull null
                                    when (index) {
                                        0 -> sliderState1
                                        else -> sliderState2
                                    } to filterParam
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            paramsInfo.forEach { (state, info) ->
                                val (title, valueRange, roundTo) = info
                                EnhancedSliderItem(
                                    enabled = !previewOnly,
                                    value = state.value,
                                    title = stringResource(title!!),
                                    valueRange = valueRange,
                                    onValueChange = {
                                        state.value = it
                                    },
                                    internalStateTransformation = {
                                        it.roundTo(roundTo)
                                    },
                                    behaveAsContainer = false
                                )
                            }
                        }
                        Text(
                            text = stringResource(filter.paramsInfo[2].title!!),
                            modifier = Modifier.padding(16.dp)
                        )
                        ColorSelectionRow(
                            value = color3,
                            allowScroll = !previewOnly,
                            allowAlpha = true,
                            onValueChange = { color ->
                                color3 = color
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        )
                    }

                    value.first is Number && value.second is Color && value.third is Color -> {
                        var sliderState1 by remember { mutableFloatStateOf((value.first as Number).toFloat()) }
                        var color1 by remember(value) { mutableStateOf(value.second as Color) }
                        var color2 by remember(value) { mutableStateOf(value.third as Color) }

                        EnhancedSliderItem(
                            modifier = Modifier
                                .padding(
                                    top = 8.dp,
                                    start = 8.dp,
                                    end = 8.dp
                                ),
                            enabled = !previewOnly,
                            value = sliderState1,
                            title = filter.paramsInfo[0].title?.let {
                                stringResource(it)
                            } ?: "",
                            onValueChange = {
                                sliderState1 = it
                                onFilterChange(Triple(sliderState1, color1, color2))
                            },
                            internalStateTransformation = {
                                it.roundTo(filter.paramsInfo[0].roundTo)
                            },
                            valueRange = filter.paramsInfo[0].valueRange,
                            behaveAsContainer = false
                        )
                        Box(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            Column {
                                Text(
                                    text = stringResource(filter.paramsInfo[1].title!!),
                                    modifier = Modifier
                                        .padding(
                                            bottom = 16.dp,
                                            top = 16.dp,
                                            end = 16.dp,
                                        )
                                )
                                ColorSelectionRow(
                                    value = color1,
                                    allowScroll = !previewOnly,
                                    allowAlpha = true,
                                    onValueChange = { color ->
                                        color1 = color
                                        onFilterChange(
                                            Triple(
                                                sliderState1,
                                                color1,
                                                color2
                                            )
                                        )
                                    }
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(filter.paramsInfo[2].title!!),
                                    modifier = Modifier
                                        .padding(
                                            top = 16.dp,
                                            bottom = 16.dp,
                                            end = 16.dp
                                        )
                                )
                                ColorSelectionRow(
                                    value = color2,
                                    allowScroll = !previewOnly,
                                    allowAlpha = true,
                                    onValueChange = { color ->
                                        color2 = color
                                        onFilterChange(
                                            Triple(
                                                sliderState1,
                                                color1,
                                                color2
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            is GlitchParams -> {
                val channelsShiftX: MutableState<Float> =
                    remember(value) { mutableFloatStateOf((value.channelsShiftX as Number).toFloat()) }
                val channelsShiftY: MutableState<Float> =
                    remember(value) { mutableFloatStateOf((value.channelsShiftY as Number).toFloat()) }
                val corruptionSize: MutableState<Float> =
                    remember(value) { mutableFloatStateOf((value.corruptionSize as Number).toFloat()) }
                val corruptionCount: MutableState<Float> =
                    remember(value) { mutableFloatStateOf((value.corruptionCount as Number).toFloat()) }
                val corruptionShiftX: MutableState<Float> =
                    remember(value) { mutableFloatStateOf((value.corruptionShiftX as Number).toFloat()) }
                val corruptionShiftY: MutableState<Float> =
                    remember(value) { mutableFloatStateOf((value.corruptionShiftY as Number).toFloat()) }

                LaunchedEffect(
                    channelsShiftX.value,
                    channelsShiftY.value,
                    corruptionSize.value,
                    corruptionCount.value,
                    corruptionShiftX.value,
                    corruptionShiftY.value
                ) {
                    onFilterChange(
                        GlitchParams(
                            channelsShiftX = channelsShiftX.value,
                            channelsShiftY = channelsShiftY.value,
                            corruptionSize = corruptionSize.value,
                            corruptionCount = corruptionCount.value.toInt(),
                            corruptionShiftX = corruptionShiftX.value,
                            corruptionShiftY = corruptionShiftY.value
                        )
                    )
                }

                val paramsInfo by remember(filter) {
                    derivedStateOf {
                        filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                            if (filterParam.title == null) return@mapIndexedNotNull null
                            when (index) {
                                0 -> channelsShiftX
                                1 -> channelsShiftY
                                2 -> corruptionSize
                                3 -> corruptionCount
                                4 -> corruptionShiftX
                                else -> corruptionShiftY
                            } to filterParam
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    paramsInfo.forEach { (state, info) ->
                        val (title, valueRange, roundTo) = info
                        EnhancedSliderItem(
                            enabled = !previewOnly,
                            value = state.value,
                            title = stringResource(title!!),
                            valueRange = valueRange,
                            onValueChange = {
                                state.value = it
                            },
                            internalStateTransformation = {
                                it.roundTo(roundTo)
                            },
                            behaveAsContainer = false
                        )
                    }
                }
            }

            is SideFadeParams.Relative -> {
                var scale by remember(value) { mutableFloatStateOf(value.scale) }
                var sideFade by remember(value) { mutableStateOf(value.side) }

                LaunchedEffect(
                    scale, sideFade
                ) {
                    onFilterChange(
                        SideFadeParams.Relative(
                            side = sideFade,
                            scale = scale
                        )
                    )
                }

                EnhancedSliderItem(
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp
                        ),
                    enabled = !previewOnly,
                    value = scale,
                    title = filter.paramsInfo[0].title?.let {
                        stringResource(it)
                    } ?: "",
                    onValueChange = {
                        scale = it
                    },
                    internalStateTransformation = {
                        it.roundTo(filter.paramsInfo[0].roundTo)
                    },
                    valueRange = filter.paramsInfo[0].valueRange,
                    behaveAsContainer = false
                )
                filter.paramsInfo[1].takeIf { it.title != null }
                    ?.let { (title, _, _) ->
                        Text(
                            text = stringResource(title!!),
                            modifier = Modifier.padding(
                                top = 8.dp,
                                start = 12.dp,
                                end = 12.dp,
                            )
                        )
                        ToggleGroupButton(
                            fadingEdgesColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            items = FadeSide.entries.map { it.translatedName },
                            selectedIndex = FadeSide.entries.indexOf(sideFade),
                            indexChanged = {
                                sideFade = FadeSide.entries[it]
                            }
                        )
                    }
            }
        }
    }
}

private val FadeSide.translatedName: String
    @Composable
    get() = when (this) {
        FadeSide.Start -> stringResource(R.string.start)
        FadeSide.End -> stringResource(R.string.end)
        FadeSide.Top -> stringResource(R.string.top)
        FadeSide.Bottom -> stringResource(R.string.bottom)
    }

private fun roundToNearestOdd(
    number: Float
): Float = number.roundToInt().let {
    if (it % 2 != 0) it
    else it + 1
}.toFloat()

private fun Float.roundTo(
    digits: Int
): Float = if (digits == NEAREST_ODD_ROUNDING) roundToNearestOdd(this)
else (this * 10f.pow(digits)).roundToInt() / (10f.pow(digits))