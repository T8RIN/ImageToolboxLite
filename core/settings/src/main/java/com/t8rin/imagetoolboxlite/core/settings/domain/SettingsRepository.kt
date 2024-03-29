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

package com.t8rin.imagetoolboxlite.core.settings.domain

import com.t8rin.imagetoolboxlite.core.settings.domain.model.CopyToClipboardMode
import com.t8rin.imagetoolboxlite.core.settings.domain.model.FontFam
import com.t8rin.imagetoolboxlite.core.settings.domain.model.NightMode
import com.t8rin.imagetoolboxlite.core.settings.domain.model.SettingsState
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun getSettingsState(): SettingsState

    fun getSettingsStateFlow(): Flow<SettingsState>

    suspend fun toggleAddSequenceNumber()

    suspend fun toggleAddOriginalFilename()

    suspend fun setEmojisCount(count: Int)

    suspend fun setImagePickerMode(mode: Int)

    suspend fun toggleAddFileSize()

    suspend fun setEmoji(emoji: Int)

    suspend fun setFilenamePrefix(name: String)

    suspend fun toggleShowDialog()

    suspend fun setColorTuple(colorTuple: String)

    suspend fun setPresets(newPresets: String)

    suspend fun toggleDynamicColors()

    suspend fun setBorderWidth(width: Float)

    suspend fun toggleAllowImageMonet()

    suspend fun toggleAmoledMode()

    suspend fun setNightMode(nightMode: NightMode)

    suspend fun setSaveFolderUri(uri: String?)

    suspend fun setColorTuples(colorTuples: String)

    suspend fun setAlignment(align: Int)

    suspend fun setScreenOrder(data: String)

    suspend fun toggleClearCacheOnLaunch()

    suspend fun toggleGroupOptionsByTypes()

    suspend fun toggleRandomizeFilename()

    suspend fun createBackupFile(): ByteArray

    suspend fun restoreFromBackupFile(
        backupFileUri: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun resetSettings()

    fun createBackupFilename(): String

    suspend fun setFont(font: FontFam)

    suspend fun setFontScale(scale: Float)

    suspend fun toggleAllowCrashlytics()

    suspend fun toggleAllowAnalytics()

    suspend fun toggleAllowBetas()

    suspend fun toggleDrawContainerShadows()

    suspend fun toggleDrawButtonShadows()

    suspend fun toggleDrawSliderShadows()

    suspend fun toggleDrawSwitchShadows()

    suspend fun toggleDrawFabShadows()

    suspend fun registerAppOpen()

    suspend fun toggleLockDrawOrientation()

    suspend fun setThemeStyle(value: Int)

    suspend fun setThemeContrast(value: Double)

    suspend fun toggleInvertColors()

    suspend fun toggleScreensSearchEnabled()

    suspend fun toggleDrawAppBarShadows()

    suspend fun setCopyToClipboardMode(copyToClipboardMode: CopyToClipboardMode)

    suspend fun setVibrationStrength(strength: Int)

    suspend fun toggleOverwriteFiles()

    suspend fun setFilenameSuffix(name: String)

    suspend fun toggleUsePixelSwitch()

    suspend fun toggleMagnifierEnabled()

    suspend fun toggleExifWidgetInitialState()

    suspend fun setInitialOCRLanguageCodes(list: List<String>)

    suspend fun getInitialOCRLanguageCodes(): List<String>

    suspend fun setScreensWithBrightnessEnforcement(data: String)

    suspend fun toggleConfettiEnabled()

    suspend fun toggleSecureMode()

    suspend fun toggleUseRandomEmojis()

    suspend fun setIconShape(iconShape: Int)

    suspend fun toggleUseEmojiAsPrimaryColor()

    suspend fun setDragHandleWidth(width: Int)

    suspend fun setConfettiType(type: Int)
}