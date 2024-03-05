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

package com.t8rin.imagetoolboxlite.presentation

import android.content.ClipData
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import com.t8rin.imagetoolboxlite.core.filters.domain.FavoriteFiltersInteractor
import com.t8rin.imagetoolboxlite.core.filters.presentation.utils.LocalFavoriteFiltersInteractor
import com.t8rin.imagetoolboxlite.core.settings.presentation.LocalEditPresetsState
import com.t8rin.imagetoolboxlite.core.settings.presentation.LocalSettingsState
import com.t8rin.imagetoolboxlite.core.settings.presentation.toUiState
import com.t8rin.imagetoolboxlite.core.ui.icons.emoji.Emoji
import com.t8rin.imagetoolboxlite.core.ui.shapes.IconShapesList
import com.t8rin.imagetoolboxlite.core.ui.theme.ImageToolboxTheme
import com.t8rin.imagetoolboxlite.core.ui.utils.confetti.ConfettiHost
import com.t8rin.imagetoolboxlite.core.ui.utils.confetti.LocalConfettiHostState
import com.t8rin.imagetoolboxlite.core.ui.utils.confetti.rememberConfettiHostState
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.ContextUtils.parseImageFromIntent
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.ReviewHandler
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.ReviewHandler.notShowReviewAgain
import com.t8rin.imagetoolboxlite.core.ui.utils.navigation.LocalNavController
import com.t8rin.imagetoolboxlite.core.ui.widget.UpdateSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.haptics.customHapticFeedback
import com.t8rin.imagetoolboxlite.core.ui.widget.other.LocalToastHostState
import com.t8rin.imagetoolboxlite.core.ui.widget.other.ToastHost
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.utils.LocalImageLoader
import com.t8rin.imagetoolboxlite.core.ui.widget.utils.setContentWithWindowSizeClass
import com.t8rin.imagetoolboxlite.feature.main.presentation.components.AppExitDialog
import com.t8rin.imagetoolboxlite.feature.main.presentation.components.EditPresetsSheet
import com.t8rin.imagetoolboxlite.feature.main.presentation.components.FirstLaunchSetupDialog
import com.t8rin.imagetoolboxlite.feature.main.presentation.components.GithubReviewDialog
import com.t8rin.imagetoolboxlite.feature.main.presentation.components.Particles
import com.t8rin.imagetoolboxlite.feature.main.presentation.components.PermissionDialog
import com.t8rin.imagetoolboxlite.feature.main.presentation.components.ScreenSelector
import com.t8rin.imagetoolboxlite.feature.main.presentation.viewModel.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : M3Activity() {

    private val viewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var favoriteFiltersInteractor: FavoriteFiltersInteractor<Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseImage(intent)

        setContentWithWindowSizeClass {
            var showExitDialog by rememberSaveable { mutableStateOf(false) }
            val editPresetsState = rememberSaveable { mutableStateOf(false) }

            var randomEmojiKey by remember {
                mutableIntStateOf(0)
            }
            val backstack = viewModel.navController.backstack.entries
            LaunchedEffect(backstack) {
                delay(200L) // Delay for transition
                randomEmojiKey++
            }

            val currentDestination by remember(backstack) {
                derivedStateOf {
                    backstack.lastOrNull()
                }
            }
            LaunchedEffect(currentDestination) {
                currentDestination?.takeIf {
                    viewModel.navController.backstack.action == NavAction.Navigate
                }?.destination?.let {
                    GlobalExceptionHandler.registerScreenOpen(it)
                }
            }

            val settingsState = viewModel.settingsState.toUiState(
                allEmojis = Emoji.allIcons(),
                allIconShapes = IconShapesList,
                randomEmojiKey = randomEmojiKey,
                getEmojiColorTuple = viewModel::getColorTupleFromEmoji
            )

            val isSecureMode = settingsState.isSecureMode
            LaunchedEffect(isSecureMode) {
                if (isSecureMode) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                }
            }

            CompositionLocalProvider(
                LocalToastHostState provides viewModel.toastHostState,
                LocalSettingsState provides settingsState,
                LocalNavController provides viewModel.navController,
                LocalEditPresetsState provides editPresetsState,
                LocalConfettiHostState provides rememberConfettiHostState(),
                LocalImageLoader provides viewModel.imageLoader,
                LocalHapticFeedback provides customHapticFeedback(viewModel.settingsState.hapticsStrength),
                LocalFavoriteFiltersInteractor provides favoriteFiltersInteractor
            ) {
                val showSelectSheet = rememberSaveable(viewModel.showSelectDialog) {
                    mutableStateOf(viewModel.showSelectDialog)
                }
                val showUpdateSheet = rememberSaveable(viewModel.showUpdateDialog) {
                    mutableStateOf(viewModel.showUpdateDialog)
                }
                LaunchedEffect(settingsState) {
                    GlobalExceptionHandler.setAllowCollectCrashlytics(settingsState.allowCollectCrashlytics)
                    GlobalExceptionHandler.setAnalyticsCollectionEnabled(settingsState.allowCollectAnalytics)
                }

                LaunchedEffect(showSelectSheet.value) {
                    if (!showSelectSheet.value) {
                        delay(600)
                        viewModel.hideSelectDialog()
                        viewModel.updateUris(null)
                    }
                }
                LaunchedEffect(showUpdateSheet.value) {
                    if (!showUpdateSheet.value) {
                        delay(600)
                        viewModel.cancelledUpdate()
                    }
                }
                val conf = LocalConfiguration.current
                val systemUiController = rememberSystemUiController()
                LaunchedEffect(conf.orientation) {
                    if (conf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        systemUiController.isNavigationBarVisible = false
                        systemUiController.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    } else {
                        systemUiController.isNavigationBarVisible = true
                        systemUiController.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                    }
                }
                ImageToolboxTheme {
                    val tiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

                    if (!tiramisu) {
                        BackHandler {
                            if (viewModel.shouldShowDialog) showExitDialog = true
                            else finishAffinity()
                        }
                    }

                    Surface(Modifier.fillMaxSize()) {
                        ScreenSelector(viewModel)

                        EditPresetsSheet(
                            editPresetsState = editPresetsState,
                            updatePresets = viewModel::setPresets
                        )

                        val clipboardManager = LocalClipboardManager.current.nativeClipboard
                        ProcessImagesPreferenceSheet(
                            uris = viewModel.uris ?: emptyList(),
                            extraImageType = viewModel.extraImageType,
                            visible = showSelectSheet,
                            navigate = { screen ->
                                viewModel.navController.navigate(screen)
                                showSelectSheet.value = false
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    clipboardManager.clearPrimaryClip()
                                } else {
                                    clipboardManager.setPrimaryClip(
                                        ClipData.newPlainText(null, "")
                                    )
                                }
                            }
                        )
                    }

                    AppExitDialog(
                        onDismiss = { showExitDialog = false },
                        visible = showExitDialog && !tiramisu
                    )

                    UpdateSheet(
                        tag = viewModel.tag,
                        changelog = viewModel.changelog,
                        visible = showUpdateSheet
                    )

                    val confettiHostState = LocalConfettiHostState.current
                    AnimatedVisibility(settingsState.isConfettiEnabled) {
                        ConfettiHost(
                            hostState = confettiHostState,
                            particles = { primary ->
                                val particlesType by remember(settingsState.confettiType) {
                                    derivedStateOf {
                                        Particles.Type.entries.first {
                                            it.ordinal == settingsState.confettiType
                                        }
                                    }
                                }

                                remember {
                                    Particles(
                                        primary = primary,
                                        context = this@AppActivity
                                    ).forType(particlesType)
                                }
                            }
                        )
                    }

                    if (!settingsState.isConfettiEnabled) {
                        SideEffect {
                            confettiHostState.currentToastData?.dismiss()
                        }
                    }

                    ToastHost(
                        hostState = LocalToastHostState.current
                    )

                    SideEffect {
                        viewModel.tryGetUpdate(
                            installedFromMarket = isInstalledFromPlayStore()
                        )
                    }

                    FirstLaunchSetupDialog(
                        toggleShowUpdateDialog = viewModel::toggleShowUpdateDialog,
                        toggleAllowBetas = viewModel::toggleAllowBetas
                    )

                    PermissionDialog()

                    if (viewModel.showGithubReviewSheet) {
                        GithubReviewDialog(
                            onDismiss = viewModel::hideReviewSheet,
                            onNotShowAgain = {
                                notShowReviewAgain(this)
                            },
                            showNotShowAgainButton = ReviewHandler.showNotShowAgainButton
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        parseImage(intent)
    }

    private fun parseImage(intent: Intent?) {
        parseImageFromIntent(
            onStart = viewModel::hideSelectDialog,
            onHasExtraImageType = viewModel::updateExtraImageType,
            onColdStart = {
                viewModel.shouldShowExitDialog(false)
            },
            onGetUris = viewModel::updateUris,
            showToast = viewModel::showToast,
            navigate = viewModel.navController::navigate,
            notHasUris = viewModel.uris.isNullOrEmpty(),
            intent = intent,
            onWantGithubReview = viewModel::onWantGithubReview
        )
    }
}