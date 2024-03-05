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

package com.t8rin.imagetoolboxlite.feature.limits_resize.presentation

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.imagetoolboxlite.core.domain.model.ImageInfo
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.settings.presentation.LocalSettingsState
import com.t8rin.imagetoolboxlite.core.ui.utils.confetti.LocalConfettiHostState
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.ImageUtils.fileSize
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.Picker
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.asClip
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.failedToSaveImages
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.localImagePickerMode
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.rememberImagePicker
import com.t8rin.imagetoolboxlite.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolboxlite.core.ui.widget.controls.ImageFormatSelector
import com.t8rin.imagetoolboxlite.core.ui.widget.controls.QualityWidget
import com.t8rin.imagetoolboxlite.core.ui.widget.controls.ResizeImageField
import com.t8rin.imagetoolboxlite.core.ui.widget.controls.SaveExifWidget
import com.t8rin.imagetoolboxlite.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolboxlite.core.ui.widget.image.ImageContainer
import com.t8rin.imagetoolboxlite.core.ui.widget.image.ImageCounter
import com.t8rin.imagetoolboxlite.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolboxlite.core.ui.widget.other.LoadingDialog
import com.t8rin.imagetoolboxlite.core.ui.widget.other.LocalToastHostState
import com.t8rin.imagetoolboxlite.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolboxlite.core.ui.widget.other.showError
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.PickImageFromUrisSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolboxlite.core.ui.widget.utils.LocalWindowSizeClass
import com.t8rin.imagetoolboxlite.feature.limits_resize.presentation.components.AutoRotateLimitBoxToggle
import com.t8rin.imagetoolboxlite.feature.limits_resize.presentation.components.LimitsResizeSelector
import com.t8rin.imagetoolboxlite.feature.limits_resize.presentation.viewModel.LimitsResizeViewModel
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun LimitsResizeScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: LimitsResizeViewModel = hiltViewModel()
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

    val pickImageLauncher = rememberImagePicker(
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
        data = viewModel.previewBitmap, visible = showZoomSheet
    )

    AdaptiveLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.limits_resize),
                input = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                size = viewModel.selectedUri?.fileSize(LocalContext.current) ?: 0L
            )
        },
        onGoBack = onBack,
        actions = {
            if (viewModel.previewBitmap != null) {
                ShareButton(
                    enabled = viewModel.canSave,
                    onShare = {
                        viewModel.shareBitmaps(showConfetti)
                    },
                    onCopy = { manager ->
                        viewModel.cacheCurrentImage { uri ->
                            manager.setClip(uri.asClip(context))
                            showConfetti()
                        }
                    }
                )
            }
            ZoomButton(
                onClick = { showZoomSheet.value = true },
                visible = viewModel.bitmap != null,
            )
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
            ResizeImageField(
                imageInfo = viewModel.imageInfo,
                originalSize = viewModel.originalSize,
                onWidthChange = viewModel::updateWidth,
                onHeightChange = viewModel::updateHeight
            )
            Spacer(Modifier.size(8.dp))
            SaveExifWidget(
                imageFormat = viewModel.imageInfo.imageFormat,
                checked = viewModel.keepExif,
                onCheckedChange = viewModel::setKeepExif
            )
            if (viewModel.imageInfo.imageFormat.canChangeCompressionValue) Spacer(
                Modifier.size(8.dp)
            )
            QualityWidget(
                imageFormat = viewModel.imageInfo.imageFormat,
                enabled = viewModel.bitmap != null,
                quality = viewModel.imageInfo.quality,
                onQualityChange = viewModel::setQuality
            )
            Spacer(Modifier.size(8.dp))
            ImageFormatSelector(
                value = viewModel.imageInfo.imageFormat,
                onValueChange = viewModel::setMime
            )
            Spacer(Modifier.size(8.dp))
            AutoRotateLimitBoxToggle(
                value = viewModel.resizeType.autoRotateLimitBox,
                onClick = viewModel::toggleAutoRotateLimitBox
            )
            Spacer(Modifier.size(8.dp))
            LimitsResizeSelector(
                enabled = viewModel.bitmap != null,
                value = viewModel.resizeType,
                onValueChange = viewModel::setResizeType
            )
        },
        noDataControls = {
            if (!viewModel.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        },
        buttons = { actions ->
            BottomButtonsBlock(
                isPrimaryButtonVisible = viewModel.canSave,
                targetState = (viewModel.uris.isNullOrEmpty()) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = saveBitmaps,
                actions = {
                    if (isPortrait) actions()
                }
            )
        },
        topAppBarPersistentActions = {
            if (viewModel.bitmap == null) {
                TopAppBarEmoji()
            }
        },
        canShowScreenData = viewModel.bitmap != null,
        isPortrait = isPortrait
    )

    if (viewModel.isSaving) {
        LoadingDialog(
            done = viewModel.done, left = viewModel.uris?.size ?: 1
        ) {
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
        onExit = onGoBack, onDismiss = { showExitDialog = false }, visible = showExitDialog
    )
}