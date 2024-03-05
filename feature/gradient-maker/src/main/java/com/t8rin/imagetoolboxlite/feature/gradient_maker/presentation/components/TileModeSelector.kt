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

package com.t8rin.imagetoolboxlite.feature.gradient_maker.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.ToggleGroupButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container

@Composable
fun TileModeSelector(
    value: TileMode,
    onValueChange: (TileMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val entries = remember {
        listOf(
            TileMode.Clamp,
            TileMode.Repeated,
            TileMode.Mirror,
            TileMode.Decal
        )
    }
    Box(
        modifier = modifier
            .container(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
            )
            .animateContentSize(),
        contentAlignment = Alignment.Center
    ) {
        ToggleGroupButton(
            modifier = Modifier.padding(8.dp),
            enabled = true,
            items = entries.map { it.translatedName },
            selectedIndex = entries.indexOf(value),
            title = stringResource(id = R.string.tile_mode),
            fadingEdgesColor = MaterialTheme.colorScheme.surfaceContainer,
            indexChanged = {
                onValueChange(entries[it])
            }
        )
    }
}

private val TileMode.translatedName: String
    @Composable
    get() = when (this) {
        TileMode.Repeated -> stringResource(id = R.string.tile_mode_repeated)
        TileMode.Mirror -> stringResource(id = R.string.tile_mode_mirror)
        TileMode.Decal -> stringResource(id = R.string.tile_mode_decal)
        else -> stringResource(id = R.string.tile_mode_clamp)
    }