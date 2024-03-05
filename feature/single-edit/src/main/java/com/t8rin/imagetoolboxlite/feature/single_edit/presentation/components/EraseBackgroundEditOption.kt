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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.theme.outlineVariant
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedIconButton
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.PanModeButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolboxlite.core.ui.widget.other.DrawLockScreenOrientation
import com.t8rin.imagetoolboxlite.core.ui.widget.text.Marquee
import com.t8rin.imagetoolboxlite.feature.draw.domain.pt
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.BrushSoftnessSelector
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.LineWidthSelector
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.PtSaver
import com.t8rin.imagetoolboxlite.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolboxlite.feature.erase_background.domain.AutoBackgroundRemover
import com.t8rin.imagetoolboxlite.feature.erase_background.presentation.components.AutoEraseBackgroundCard
import com.t8rin.imagetoolboxlite.feature.erase_background.presentation.components.BitmapEraser
import com.t8rin.imagetoolboxlite.feature.erase_background.presentation.components.OriginalImagePreviewAlphaSelector
import com.t8rin.imagetoolboxlite.feature.erase_background.presentation.components.RecoverModeButton
import com.t8rin.imagetoolboxlite.feature.erase_background.presentation.components.RecoverModeCard
import com.t8rin.imagetoolboxlite.feature.erase_background.presentation.components.TrimImageToggle
import com.t8rin.imagetoolboxlite.feature.erase_background.presentation.components.UseLassoSelector
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EraseBackgroundEditOption(
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    onGetBitmap: (Bitmap) -> Unit,
    clearErasing: (Boolean) -> Unit,
    undo: () -> Unit,
    redo: () -> Unit,
    paths: List<UiPathPaint>,
    lastPaths: List<UiPathPaint>,
    undonePaths: List<UiPathPaint>,
    addPath: (UiPathPaint) -> Unit,
    autoBackgroundRemover: AutoBackgroundRemover<Bitmap>
) {
    val scope = rememberCoroutineScope()

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

        var isRecoveryOn by rememberSaveable { mutableStateOf(false) }

        var strokeWidth by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(20.pt) }
        var brushSoftness by rememberSaveable(stateSaver = PtSaver) {
            mutableStateOf(0.pt)
        }
        var useLasso by rememberSaveable {
            mutableStateOf(false)
        }
        var originalImagePreviewAlpha by rememberSaveable {
            mutableFloatStateOf(0.2f)
        }

        var trimImage by rememberSaveable { mutableStateOf(true) }

        val secondaryControls = @Composable {
            Row(
                modifier = Modifier
                    .then(
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
                RecoverModeButton(
                    selected = isRecoveryOn,
                    enabled = !panEnabled,
                    onClick = { isRecoveryOn = !isRecoveryOn }
                )
            }
        }

        var bitmapState by remember(bitmap, visible) { mutableStateOf(bitmap) }
        var erasedBitmap by remember(bitmap, visible) { mutableStateOf(bitmap) }

        FullscreenEditOption(
            canGoBack = paths.isEmpty(),
            visible = visible,
            onDismiss = onDismiss,
            useScaffold = useScaffold,
            controls = { _ ->
                if (!useScaffold) secondaryControls()
                Spacer(modifier = Modifier.height(8.dp))
                RecoverModeCard(
                    selected = isRecoveryOn,
                    enabled = !panEnabled,
                    onClick = { isRecoveryOn = !isRecoveryOn }
                )
                AutoEraseBackgroundCard(
                    onReset = {
                        bitmapState = bitmap
                    }
                )
                OriginalImagePreviewAlphaSelector(
                    value = originalImagePreviewAlpha,
                    onValueChange = {
                        originalImagePreviewAlpha = it
                    },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )
                UseLassoSelector(
                    value = useLasso,
                    onValueChange = {
                        useLasso = it
                    },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                )
                LineWidthSelector(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    value = strokeWidth.value,
                    onValueChange = { strokeWidth = it.pt }
                )
                BrushSoftnessSelector(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 16.dp, start = 16.dp),
                    value = brushSoftness.value,
                    onValueChange = { brushSoftness = it.pt }
                )
                TrimImageToggle(
                    checked = trimImage,
                    onCheckedChange = { trimImage = it },
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp
                    )
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
                                    scope.launch {
                                        onGetBitmap(
                                            if (trimImage) autoBackgroundRemover.trimEmptyParts(
                                                erasedBitmap
                                            ) else erasedBitmap
                                        )
                                        clearErasing(false)
                                    }
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
                                text = stringResource(R.string.erase_background),
                            )
                        }
                    }
                )
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                AnimatedContent(
                    targetState = remember(bitmapState) {
                        derivedStateOf {
                            bitmapState.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                        }
                    }.value,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { imageBitmap ->
                    val direction = LocalLayoutDirection.current
                    val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
                    BitmapEraser(
                        imageBitmapForShader = bitmap.asImageBitmap(),
                        imageBitmap = imageBitmap,
                        paths = paths,
                        strokeWidth = strokeWidth,
                        brushSoftness = brushSoftness,
                        onAddPath = addPath,
                        isRecoveryOn = isRecoveryOn,
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
                        useLasso = useLasso,
                        originalImagePreviewAlpha = originalImagePreviewAlpha,
                        onErased = { erasedBitmap = it }
                    )
                }
            }
        }

        if (visible) {
            DrawLockScreenOrientation()
        }
    }
}