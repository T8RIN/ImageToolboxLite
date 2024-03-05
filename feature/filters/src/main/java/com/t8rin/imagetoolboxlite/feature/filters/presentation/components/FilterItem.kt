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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolboxlite.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolboxlite.core.settings.presentation.LocalSettingsState
import com.t8rin.imagetoolboxlite.core.ui.theme.outlineVariant
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedIconButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolboxlite.core.ui.widget.value.ValueDialog
import com.t8rin.imagetoolboxlite.core.ui.widget.value.ValueText

@Composable
fun <T> FilterItem(
    filter: UiFilter<T>,
    showDragHandle: Boolean,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    previewOnly: Boolean = false,
    onFilterChange: (value: Any) -> Unit,
    backgroundColor: Color = MaterialTheme
        .colorScheme
        .surfaceContainerLow,
    shape: Shape = MaterialTheme.shapes.extraLarge
) {
    val settingsState = LocalSettingsState.current

    var isControlsExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier.then(
            onLongPress?.let {
                Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { it() }
                    )
                }
            } ?: Modifier
        )
    ) {
        Row(
            modifier = modifier
                .container(color = backgroundColor, shape = shape)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(if (previewOnly) 0.5f else 1f)
                    .then(
                        if (previewOnly) {
                            Modifier
                                .heightIn(max = 120.dp)
                                .fadingEdges(
                                    scrollableState = null,
                                    isVertical = true,
                                    length = 12.dp
                                )
                        } else Modifier
                    )
            ) {
                var sliderValue by remember(filter) {
                    mutableFloatStateOf(
                        ((filter.value as? Number)?.toFloat()) ?: 0f
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!previewOnly) {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onRemove
                        ) {
                            Icon(Icons.Rounded.RemoveCircleOutline, null)
                        }
                    }
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(filter.title),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(
                                    top = 8.dp,
                                    end = 8.dp,
                                    start = 16.dp,
                                    bottom = 8.dp
                                )
                                .fillMaxWidth()
                        )
                    }
                    if (!filter.value.isSingle() && !previewOnly) {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = {
                                isControlsExpanded = !isControlsExpanded
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.rotate(
                                    animateFloatAsState(
                                        if (isControlsExpanded) 180f
                                        else 0f
                                    ).value
                                )
                            )
                        }
                    }
                    if (filter.value is Number) {
                        var showValueDialog by remember { mutableStateOf(false) }
                        ValueText(
                            value = sliderValue,
                            onClick = { showValueDialog = true }
                        )
                        ValueDialog(
                            roundTo = filter.paramsInfo[0].roundTo,
                            valueRange = filter.paramsInfo[0].valueRange,
                            valueState = sliderValue.toString(),
                            expanded = showValueDialog && !previewOnly,
                            onDismiss = { showValueDialog = false },
                            onValueUpdate = {
                                sliderValue = it
                                onFilterChange(it)
                            }
                        )
                    }
                }
                AnimatedVisibility(
                    visible = isControlsExpanded || filter.value.isSingle() || previewOnly
                ) {
                    FilterItemContent(
                        filter = filter,
                        onFilterChange = onFilterChange,
                        previewOnly = previewOnly
                    )
                }
            }
            if (showDragHandle) {
                Box(
                    modifier = Modifier
                        .height(if (filter.value is Unit) 32.dp else 64.dp)
                        .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant())
                        .padding(start = 20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.DragHandle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
        }
        if (previewOnly) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.matchParentSize()
            ) {}
        }
    }
}

private fun Any?.isSingle(): Boolean = this is Number || this is Unit