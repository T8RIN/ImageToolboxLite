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

package com.t8rin.imagetoolboxlite.feature.draw.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.settings.presentation.LocalSettingsState
import com.t8rin.imagetoolboxlite.core.ui.icons.material.FreeArrow
import com.t8rin.imagetoolboxlite.core.ui.icons.material.FreeDoubleArrow
import com.t8rin.imagetoolboxlite.core.ui.icons.material.FreeDraw
import com.t8rin.imagetoolboxlite.core.ui.icons.material.Lasso
import com.t8rin.imagetoolboxlite.core.ui.icons.material.Line
import com.t8rin.imagetoolboxlite.core.ui.icons.material.LineArrow
import com.t8rin.imagetoolboxlite.core.ui.icons.material.LineDoubleArrow
import com.t8rin.imagetoolboxlite.core.ui.icons.material.Square
import com.t8rin.imagetoolboxlite.core.ui.theme.outlineVariant
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedButton
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.ContainerShapeDefaults
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.materialShadow
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.SimpleSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolboxlite.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolboxlite.feature.draw.domain.DrawPathMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawPathModeSelector(
    modifier: Modifier,
    values: List<DrawPathMode> = DrawPathMode.entries,
    value: DrawPathMode,
    onValueChange: (DrawPathMode) -> Unit,
    containerColor: Color = Color.Unspecified
) {
    val haptics = LocalHapticFeedback.current
    val state = rememberSaveable { mutableStateOf(false) }

    val settingsState = LocalSettingsState.current
    Column(
        modifier = modifier
            .container(
                shape = RoundedCornerShape(24.dp),
                color = containerColor
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.draw_path_mode),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            SupportingButton(
                onClick = {
                    state.value = true
                }
            )
        }
        Box {
            SingleChoiceSegmentedButtonRow(
                space = max(settingsState.borderWidth, 1.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 6.dp, end = 6.dp, bottom = 8.dp, top = 8.dp)
            ) {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentEnforcement provides false
                ) {
                    values.forEachIndexed { index, item ->
                        val selected by remember(value, item) {
                            derivedStateOf {
                                value::class.isInstance(item)
                            }
                        }
                        val shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = values.size
                        )
                        SegmentedButton(
                            onClick = {
                                haptics.performHapticFeedback(
                                    HapticFeedbackType.LongPress
                                )
                                onValueChange(item)
                            },
                            selected = selected,
                            icon = {},
                            border = BorderStroke(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant()
                            ),
                            colors = SegmentedButtonDefaults.colors(
                                activeBorderColor = MaterialTheme.colorScheme.outlineVariant(),
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    1.dp
                                ),
                                activeContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    10.dp
                                )
                            ),
                            modifier = Modifier.materialShadow(
                                shape = shape,
                                elevation = animateDpAsState(
                                    if (settingsState.borderWidth >= 0.dp || !settingsState.drawButtonShadows) 0.dp
                                    else if (selected) 2.dp
                                    else 1.dp
                                ).value
                            ),
                            shape = shape
                        ) {
                            Icon(
                                imageVector = item.getIcon(),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(8.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to containerColor,
                            1f to Color.Transparent
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(8.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to Color.Transparent,
                            1f to containerColor
                        )
                    )
            )
        }
    }
    SimpleSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                values.forEachIndexed { index, item ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ContainerShapeDefaults.shapeForIndex(
                                    index = index,
                                    size = values.size
                                ),
                                resultPadding = 0.dp
                            )
                    ) {
                        TitleItem(text = stringResource(item.getTitle()), icon = item.getIcon())
                        Text(
                            text = stringResource(item.getSubtitle()),
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        visible = state,
        title = {
            TitleItem(text = stringResource(R.string.draw_path_mode))
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { state.value = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}

private fun DrawPathMode.getSubtitle(): Int =
    when (this) {
        DrawPathMode.DoubleLinePointingArrow -> R.string.double_line_arrow_sub
        DrawPathMode.DoublePointingArrow -> R.string.double_arrow_sub
        DrawPathMode.Free -> R.string.free_drawing_sub
        DrawPathMode.Line -> R.string.line_sub
        DrawPathMode.LinePointingArrow -> R.string.line_arrow_sub
        DrawPathMode.PointingArrow -> R.string.arrow_sub
        DrawPathMode.OutlinedOval -> R.string.outlined_oval_sub
        DrawPathMode.OutlinedRect -> R.string.outlined_rect_sub
        DrawPathMode.Oval -> R.string.oval_sub
        DrawPathMode.Rect -> R.string.rect_sub
        DrawPathMode.Lasso -> R.string.lasso_sub
    }

private fun DrawPathMode.getTitle(): Int =
    when (this) {
        DrawPathMode.DoubleLinePointingArrow -> R.string.double_line_arrow
        DrawPathMode.DoublePointingArrow -> R.string.double_arrow
        DrawPathMode.Free -> R.string.free_drawing
        DrawPathMode.Line -> R.string.line
        DrawPathMode.LinePointingArrow -> R.string.line_arrow
        DrawPathMode.PointingArrow -> R.string.arrow
        DrawPathMode.OutlinedOval -> R.string.outlined_oval
        DrawPathMode.OutlinedRect -> R.string.outlined_rect
        DrawPathMode.Oval -> R.string.oval
        DrawPathMode.Rect -> R.string.rect
        DrawPathMode.Lasso -> R.string.lasso
    }

private fun DrawPathMode.getIcon(): ImageVector =
    when (this) {
        DrawPathMode.DoubleLinePointingArrow -> Icons.Rounded.LineDoubleArrow
        DrawPathMode.DoublePointingArrow -> Icons.Rounded.FreeDoubleArrow
        DrawPathMode.Free -> Icons.Rounded.FreeDraw
        DrawPathMode.Line -> Icons.Rounded.Line
        DrawPathMode.LinePointingArrow -> Icons.Rounded.LineArrow
        DrawPathMode.PointingArrow -> Icons.Rounded.FreeArrow
        DrawPathMode.OutlinedOval -> Icons.Rounded.RadioButtonUnchecked
        DrawPathMode.OutlinedRect -> Icons.Rounded.CheckBoxOutlineBlank
        DrawPathMode.Oval -> Icons.Rounded.Circle
        DrawPathMode.Rect -> Icons.Rounded.Square
        DrawPathMode.Lasso -> Icons.Rounded.Lasso
    }