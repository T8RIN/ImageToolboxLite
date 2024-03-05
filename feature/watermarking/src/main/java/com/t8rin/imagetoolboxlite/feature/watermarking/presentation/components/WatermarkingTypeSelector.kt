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

package com.t8rin.imagetoolboxlite.feature.watermarking.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.ToggleGroupButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container
import com.t8rin.imagetoolboxlite.feature.watermarking.domain.WatermarkParams
import com.t8rin.imagetoolboxlite.feature.watermarking.domain.WatermarkingType

@Composable
fun WatermarkingTypeSelector(
    value: WatermarkParams,
    onValueChange: (WatermarkParams) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex by remember(value.watermarkingType) {
        derivedStateOf {
            WatermarkingType
                .entries
                .indexOfFirst {
                    value.watermarkingType::class.java.isInstance(it)
                }
        }
    }
    ToggleGroupButton(
        modifier = modifier
            .container(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ),
        enabled = true,
        items = WatermarkingType.entries.map { it.translatedName },
        selectedIndex = selectedIndex,
        title = stringResource(id = R.string.watermark_type),
        fadingEdgesColor = MaterialTheme.colorScheme.surfaceContainerLow,
        indexChanged = {
            onValueChange(value.copy(watermarkingType = WatermarkingType.entries[it]))
        },
        inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

private val WatermarkingType.translatedName: String
    @Composable
    get() = when (this) {
        is WatermarkingType.Text -> stringResource(R.string.text)
        is WatermarkingType.Image -> stringResource(R.string.image)
    }