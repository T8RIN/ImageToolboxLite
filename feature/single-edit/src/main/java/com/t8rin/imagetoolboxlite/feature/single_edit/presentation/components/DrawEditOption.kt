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

package com.t8rin.imagetoolboxlite.feature.single_edit.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolboxlite.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.theme.outlineVariant
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedIconButton
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EraseModeButton
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.PanModeButton
import com.t8rin.imagetoolboxlite.core.ui.widget.controls.AlphaSelector
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolboxlite.core.ui.widget.other.DrawLockScreenOrientation
import com.t8rin.imagetoolboxlite.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolboxlite.core.ui.widget.text.Marquee
import com.t8rin.imagetoolboxlite.feature.draw.domain.DrawMode
import com.t8rin.imagetoolboxlite.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolboxlite.feature.draw.domain.pt
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.BitmapDrawer
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.BrushSoftnessSelector
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.DrawColorSelector
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.DrawModeSaver
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.DrawModeSelector
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.DrawPathModeSaver
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.DrawPathModeSelector
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.LineWidthSelector
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.OpenColorPickerCard
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.PtSaver
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolboxlite.feature.pick_color.presentation.components.PickColorFromImageSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawEditOption(
    visible: Boolean,
    onRequestFiltering: suspend (Bitmap, List<UiFilter<*>>) -> Bitmap?,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    onGetBitmap: (Bitmap) -> Unit,
    undo: () -> Unit,
    redo: () -> Unit,
    paths: List<UiPathPaint>,
    lastPaths: List<UiPathPaint>,
    undonePaths: List<UiPathPaint>,
    addPath: (UiPathPaint) -> Unit,
) {
    bitmap?.let {
        var panEnabled by rememberSaveable { mutableStateOf(false) }

        val switch = @Composable {
            PanModeButton(
                selected = panEnabled,
                onClick = {
                    panEnabled = !panEnabled
                }
            )
        }

        val showPickColorSheet = rememberSaveable { mutableStateOf(false) }

        var isEraserOn by rememberSaveable { mutableStateOf(false) }

        var strokeWidth by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(20.pt) }
        var drawColor by rememberSaveable(
            stateSaver = ColorSaver
        ) { mutableStateOf(Color.Black) }
        var drawMode by rememberSaveable(stateSaver = DrawModeSaver) { mutableStateOf(DrawMode.Pen) }
        var alpha by rememberSaveable(drawMode) {
            mutableFloatStateOf(if (drawMode is DrawMode.Highlighter) 0.4f else 1f)
        }
        var brushSoftness by rememberSaveable(drawMode, stateSaver = PtSaver) {
            mutableStateOf(if (drawMode is DrawMode.Neon) 35.pt else 0.pt)
        }
        var drawPathMode by rememberSaveable(stateSaver = DrawPathModeSaver) {
            mutableStateOf(DrawPathMode.Free)
        }

        val secondaryControls = @Composable {
            Row(
                modifier = Modifier.then(
                    if (!useScaffold) {
                        Modifier
                            .padding(16.dp)
                            .container(shape = CircleShape)
                    } else Modifier
                )
            ) {
                switch()
                Spacer(Modifier.width(8.dp))
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        luminance = 0.1f
                    ),
                    onClick = undo,
                    enabled = lastPaths.isNotEmpty() || paths.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Undo, null)
                }
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        luminance = 0.1f
                    ),
                    onClick = redo,
                    enabled = undonePaths.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Redo, null)
                }
                EraseModeButton(
                    selected = isEraserOn,
                    enabled = !panEnabled,
                    onClick = {
                        isEraserOn = !isEraserOn
                    }
                )
            }
        }

        var stateBitmap by remember(bitmap, visible) { mutableStateOf(bitmap) }
        FullscreenEditOption(
            canGoBack = paths.isEmpty(),
            visible = visible,
            onDismiss = onDismiss,
            useScaffold = useScaffold,
            controls = {
                if (!useScaffold) secondaryControls()
                OpenColorPickerCard(
                    onOpen = {
                        showPickColorSheet.value = true
                    }
                )
                LineWidthSelector(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp
                    ),
                    value = strokeWidth.value,
                    onValueChange = { strokeWidth = it.pt }
                )
                AnimatedVisibility(
                    visible = drawMode !is DrawMode.Highlighter && drawMode !is DrawMode.PathEffect,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    BrushSoftnessSelector(
                        modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp, start = 16.dp),
                        value = brushSoftness.value,
                        onValueChange = { brushSoftness = it.pt }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = drawMode !is DrawMode.PathEffect,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    DrawColorSelector(
                        drawColor = drawColor,
                        onColorChange = { drawColor = it }
                    )
                }
                AnimatedVisibility(
                    visible = drawMode !is DrawMode.Neon && drawMode !is DrawMode.PathEffect,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AlphaSelector(
                        value = alpha,
                        onValueChange = { alpha = it }
                    )
                }
                DrawModeSelector(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    value = drawMode,
                    onValueChange = { drawMode = it }
                )
                DrawPathModeSelector(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    value = drawPathMode,
                    onValueChange = { drawPathMode = it },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            },
            fabButtons = null,
            actions = {
                if (useScaffold) {
                    secondaryControls()
                    Spacer(Modifier.weight(1f))
                }
            },
            topAppBar = { closeButton ->
                CenterAlignedTopAppBar(
                    navigationIcon = closeButton,
                    colors = TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    modifier = Modifier.drawHorizontalStroke(),
                    actions = {
                        AnimatedVisibility(
                            visible = paths.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            EnhancedIconButton(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                onClick = {
                                    onGetBitmap(stateBitmap)
                                    onDismiss()
                                }
                            ) {
                                Icon(Icons.Rounded.Done, null)
                            }
                        }
                    },
                    title = {
                        Marquee(edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) {
                            Text(
                                text = stringResource(R.string.draw),
                            )
                        }
                    }
                )
            }
        ) {
            val direction = LocalLayoutDirection.current
            Box(contentAlignment = Alignment.Center) {
                remember(bitmap) {
                    derivedStateOf {
                        bitmap.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                    }
                }.value.let { imageBitmap ->
                    val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
                    BitmapDrawer(
                        imageBitmap = imageBitmap,
                        paths = paths,
                        onRequestFiltering = onRequestFiltering,
                        strokeWidth = strokeWidth,
                        brushSoftness = brushSoftness,
                        drawColor = drawColor.copy(alpha),
                        onAddPath = addPath,
                        isEraserOn = isEraserOn,
                        drawMode = drawMode,
                        modifier = Modifier
                            .padding(
                                start = WindowInsets
                                    .displayCutout
                                    .asPaddingValues()
                                    .calculateStartPadding(direction)
                            )
                            .padding(16.dp)
                            .aspectRatio(aspectRatio, !useScaffold)
                            .fillMaxSize(),
                        panEnabled = panEnabled,
                        onDraw = {
                            stateBitmap = it
                        },
                        drawPathMode = drawPathMode,
                        backgroundColor = Color.Transparent
                    )
                }
            }
        }
        var color by rememberSaveable(stateSaver = ColorSaver) {
            mutableStateOf(Color.Black)
        }
        PickColorFromImageSheet(
            visible = showPickColorSheet,
            bitmap = stateBitmap,
            onColorChange = { color = it },
            color = color
        )

        if (visible) {
            DrawLockScreenOrientation()
        }
    }
}