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

package com.t8rin.imagetoolboxlite.feature.bytes_resize.presentation

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.imagetoolboxlite.core.domain.model.ImageInfo
import com.t8rin.imagetoolboxlite.core.domain.model.Preset
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.settings.presentation.LocalSettingsState
import com.t8rin.imagetoolboxlite.core.ui.utils.confetti.LocalConfettiHostState
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.ImageUtils.restrict
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.Picker
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.asClip
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.failedToSaveImages
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.localImagePickerMode
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.rememberImagePicker
import com.t8rin.imagetoolboxlite.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.PanModeButton
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolboxlite.core.ui.widget.controls.ImageFormatSelector
import com.t8rin.imagetoolboxlite.core.ui.widget.controls.PresetSelector
import com.t8rin.imagetoolboxlite.core.ui.widget.controls.SaveExifWidget
import com.t8rin.imagetoolboxlite.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolboxlite.core.ui.widget.image.ImageContainer
import com.t8rin.imagetoolboxlite.core.ui.widget.image.ImageCounter
import com.t8rin.imagetoolboxlite.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container
import com.t8rin.imagetoolboxlite.core.ui.widget.other.LoadingDialog
import com.t8rin.imagetoolboxlite.core.ui.widget.other.LocalToastHostState
import com.t8rin.imagetoolboxlite.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolboxlite.core.ui.widget.other.showError
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.PickImageFromUrisSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolboxlite.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolboxlite.core.ui.widget.utils.LocalWindowSizeClass
import com.t8rin.imagetoolboxlite.feature.bytes_resize.presentation.components.ImageFormatAlert
import com.t8rin.imagetoolboxlite.feature.bytes_resize.presentation.viewModel.BytesResizeViewModel
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun BytesResizeScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: BytesResizeViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current

    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.updateUris(uris) {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            }
        }
    }
    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(it)
            }
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let { uris ->
                viewModel.updateUris(uris) {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.canSave) showExitDialog = true
        else onGoBack()
    }


    val saveBitmaps: () -> Unit = {
        viewModel.saveBitmaps { results, savingPath ->
            context.failedToSaveImages(
                scope = scope,
                results = results,
                toastHostState = toastHostState,
                savingPathString = savingPath,
                isOverwritten = settingsState.overwriteFiles,
                showConfetti = showConfetti
            )
        }
    }

    val showPickImageFromUrisSheet = rememberSaveable { mutableStateOf(false) }

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

    ZoomModalSheet(
        data = viewModel.previewBitmap,
        visible = showZoomSheet
    )

    AdaptiveLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.by_bytes_resize),
                input = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                size = viewModel.imageSize
            )
        },
        onGoBack = onBack,
        actions = {},
        topAppBarPersistentActions = {
            if (viewModel.bitmap == null) {
                TopAppBarEmoji()
            }
            ZoomButton(
                onClick = { showZoomSheet.value = true },
                visible = viewModel.bitmap != null,
            )
            if (viewModel.previewBitmap != null) {
                ShareButton(
                    enabled = viewModel.canSave,
                    onShare = {
                        viewModel.shareBitmaps { showConfetti() }
                    },
                    onCopy = { manager ->
                        viewModel.cacheCurrentImage { uri ->
                            manager.setClip(uri.asClip(context))
                            showConfetti()
                        }
                    }
                )
            }
        },
        imagePreview = {
            ImageContainer(
                imageInside = isPortrait,
                showOriginal = false,
                previewBitmap = viewModel.previewBitmap,
                originalBitmap = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                shouldShowPreview = true
            )
        },
        controls = {
            ImageCounter(
                imageCount = viewModel.uris?.size?.takeIf { it > 1 },
                onRepick = {
                    showPickImageFromUrisSheet.value = true
                }
            )
            AnimatedContent(
                targetState = viewModel.handMode,
                transitionSpec = {
                    if (!targetState) {
                        slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
                    } else {
                        slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
                    }
                }
            ) { handMode ->
                if (handMode) {
                    RoundedTextField(
                        modifier = Modifier
                            .container(shape = RoundedCornerShape(24.dp))
                            .padding(8.dp),
                        enabled = viewModel.bitmap != null,
                        value = (viewModel.maxBytes / 1024).toString()
                            .takeIf { it != "0" } ?: "",
                        onValueChange = {
                            viewModel.updateMaxBytes(
                                it.restrict(1_000_000)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        label = stringResource(R.string.max_bytes)
                    )
                } else {
                    PresetSelector(
                        value = viewModel.presetSelected.let {
                            Preset.Numeric(it)
                        },
                        includeTelegramOption = false,
                        onValueChange = viewModel::selectPreset
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            SaveExifWidget(
                imageFormat = viewModel.imageFormat,
                checked = viewModel.keepExif,
                onCheckedChange = viewModel::setKeepExif
            )
            AnimatedVisibility(
                visible = viewModel.imageFormat.canChangeCompressionValue
            ) {
                Spacer(Modifier.height(8.dp))
            }
            ImageFormatAlert(
                format = viewModel.imageFormat,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            ImageFormatSelector(
                value = viewModel.imageFormat,
                onValueChange = viewModel::setImageFormat
            )
        },
        buttons = {
            BottomButtonsBlock(
                targetState = (viewModel.uris.isNullOrEmpty()) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = saveBitmaps,
                isPrimaryButtonVisible = viewModel.canSave,
                actions = {
                    PanModeButton(
                        selected = viewModel.handMode,
                        onClick = viewModel::updateHandMode
                    )
                }
            )
        },
        canShowScreenData = viewModel.bitmap != null,
        noDataControls = {
            if (!viewModel.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        },
        isPortrait = isPortrait
    )

    if (viewModel.isSaving) {
        LoadingDialog(viewModel.done, viewModel.uris?.size ?: 1) {
            viewModel.cancelSaving()
        }
    }

    PickImageFromUrisSheet(
        transformations = listOf(
            viewModel.imageInfoTransformationFactory(
                imageInfo = ImageInfo()
            )
        ),
        visible = showPickImageFromUrisSheet,
        uris = viewModel.uris,
        selectedUri = viewModel.selectedUri,
        onUriPicked = { uri ->
            try {
                viewModel.setBitmap(uri = uri)
            } catch (e: Exception) {
                scope.launch {
                    toastHostState.showError(context, e)
                }
            }
        },
        onUriRemoved = { uri ->
            viewModel.updateUrisSilently(removedUri = uri)
        },
        columns = if (isPortrait) 2 else 4,
    )

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}