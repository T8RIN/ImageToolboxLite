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

package com.t8rin.imagetoolboxlite.feature.compare.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedIconButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container

@Composable
fun CompareSelectionButtons(
    value: CompareType,
    onValueChange: (CompareType) -> Unit,
    isPortrait: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonsContent = @Composable {
        CompareType.entries.forEach { compareType ->
            val selected by remember(compareType, value) {
                derivedStateOf {
                    compareType == value
                }
            }
            val containerColor by animateColorAsState(
                if (selected) MaterialTheme.colorScheme.secondaryContainer
                else Color.Transparent
            )
            EnhancedIconButton(
                containerColor = containerColor,
                onClick = { onValueChange(compareType) }
            ) {
                Icon(
                    imageVector = compareType.icon,
                    contentDescription = null
                )
            }
        }
    }
    val internalModifier = modifier.container(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = CircleShape
    )
    if (isPortrait) {
        Row(
            modifier = internalModifier
        ) {
            buttonsContent()
        }
    } else {
        Column(
            modifier = internalModifier
        ) {
            buttonsContent()
        }
    }
}