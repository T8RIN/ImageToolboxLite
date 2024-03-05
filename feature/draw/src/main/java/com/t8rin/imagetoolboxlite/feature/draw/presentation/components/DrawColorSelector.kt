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

package com.t8rin.imagetoolboxlite.feature.draw.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.widget.color_picker.ColorSelectionRow
import com.t8rin.imagetoolboxlite.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container

@Composable
fun DrawColorSelector(
    modifier: Modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    drawColor: Color,
    onColorChange: (Color) -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    titleText: String = stringResource(R.string.paint_color),
    defaultColors: List<Color> = ColorSelectionRowDefaults.colorList,
) {
    Column(
        modifier = modifier
            .container(
                shape = RoundedCornerShape(24.dp),
                color = color
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = titleText,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 18.sp
            )
        }
        ColorSelectionRow(
            defaultColors = defaultColors,
            value = drawColor,
            contentPadding = PaddingValues(horizontal = 16.dp),
            onValueChange = onColorChange
        )
    }
}