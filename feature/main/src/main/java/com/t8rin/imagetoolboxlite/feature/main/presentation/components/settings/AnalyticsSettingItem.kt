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

package com.t8rin.imagetoolboxlite.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.settings.presentation.LocalSettingsState
import com.t8rin.imagetoolboxlite.core.ui.icons.material.Analytics
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.ContainerShapeDefaults
import com.t8rin.imagetoolboxlite.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun AnalyticsSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = ContainerShapeDefaults.bottomShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        shape = shape,
        modifier = modifier,
        title = stringResource(R.string.analytics),
        subtitle = stringResource(id = R.string.analytics_sub),
        startIcon = Icons.Rounded.Analytics,
        checked = settingsState.allowCollectAnalytics,
        onClick = onClick
    )
}