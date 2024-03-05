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

package com.t8rin.imagetoolboxlite.feature.image_stitch.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.ToggleGroupButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container

@Composable
fun ImageFadingEdgesSelector(
    modifier: Modifier = Modifier,
    value: Int?,
    onValueChange: (Int?) -> Unit
) {
    Column(
        modifier = modifier
            .container(shape = RoundedCornerShape(24.dp))
    ) {
        ToggleGroupButton(
            modifier = Modifier.padding(start = 3.dp, end = 2.dp),
            enabled = true,
            title = {
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(id = R.string.fading_edges))
                Spacer(modifier = Modifier.height(8.dp))
            },
            items = listOf(
                stringResource(R.string.disabled),
                stringResource(R.string.start),
                stringResource(R.string.both)
            ),
            selectedIndex = when (value) {
                null -> 0
                0 -> 1
                else -> 2
            },
            indexChanged = {
                onValueChange(
                    when (it) {
                        0 -> null
                        1 -> 0
                        else -> 1
                    }
                )
            }
        )
    }
}