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

package com.t8rin.imagetoolboxlite.feature.delete_exif.presentation


import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import com.t8rin.imagetoolboxlite.core.domain.model.ImageInfo
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.settings.presentation.LocalSettingsState
import com.t8rin.imagetoolboxlite.core.ui.icons.material.Exif
import com.t8rin.imagetoolboxlite.core.ui.icons.material.MiniEdit
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
import com.t8rin.imagetoolboxlite.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolboxlite.core.ui.widget.image.ImageContainer
import com.t8rin.imagetoolboxlite.core.ui.widget.image.ImageCounter
import com.t8rin.imagetoolboxlite.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolboxlite.core.ui.widget.other.LoadingDialog
import com.t8rin.imagetoolboxlite.core.ui.widget.other.LocalToastHostState
import com.t8rin.imagetoolboxlite.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolboxlite.core.ui.widget.other.showError
import com.t8rin.imagetoolboxlite.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.AddExifSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.PickImageFromUrisSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolboxlite.core.ui.widget.utils.LocalWindowSizeClass
import com.t8rin.imagetoolboxlite.feature.delete_exif.presentation.viewModel.DeleteExifViewModel

@Composable
fun DeleteExifScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: DeleteExifViewModel = hiltViewModel()
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
                title = stringResource(R.string.delete_exif),
                input = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                size = viewModel.selectedUri?.fileSize(LocalContext.current) ?: 0L
            )
        },
        onGoBack = {
            if (viewModel.uris?.isNotEmpty() == true) showExitDialog = true
            else onGoBack()
        },
        actions = {
            if (viewModel.previewBitmap != null) {
                ShareButton(
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
        topAppBarPersistentActions = {
            if (viewModel.bitmap == null) {
                TopAppBarEmoji()
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
            var showExifSelection by rememberSaveable {
                mutableStateOf(false)
            }
            val selectedTags = viewModel.selectedTags
            val subtitle by remember(selectedTags) {
                derivedStateOf {
                    if (selectedTags.isEmpty()) context.getString(R.string.all)
                    else selectedTags.joinToString(", ")
                }
            }
            ImageCounter(
                imageCount = viewModel.uris?.size?.takeIf { it > 1 },
                onRepick = {
                    showPickImageFromUrisSheet.value = true
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            PreferenceItem(
                onClick = {
                    showExifSelection = true
                },
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.tags_to_remove),
                subtitle = subtitle,
                shape = RoundedCornerShape(24.dp),
                startIcon = Icons.Rounded.Exif,
                endIcon = Icons.Rounded.MiniEdit
            )

            AddExifSheet(
                visible = showExifSelection,
                onDismiss = { showExifSelection = it },
                selectedTags = selectedTags,
                onTagSelected = viewModel::addTag,
                isTagsRemovable = true
            )
        },
        buttons = { actions ->
            BottomButtonsBlock(
                targetState = (viewModel.uris.isNullOrEmpty()) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = saveBitmaps,
                actions = {
                    if (isPortrait) actions()
                }
            )
        },
        noDataControls = {
            ImageNotPickedWidget(onPickImage = pickImage)
        },
        canShowScreenData = !viewModel.uris.isNullOrEmpty(),
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
            viewModel.setUri(uri = uri) {
                scope.launch {
                    toastHostState.showError(context, it)
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