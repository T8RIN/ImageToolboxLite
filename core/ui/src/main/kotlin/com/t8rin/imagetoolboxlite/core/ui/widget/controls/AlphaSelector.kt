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

package com.t8rin.imagetoolboxlite.core.ui.widget.controls

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolboxlite.core.resources.R

@Composable
fun AlphaSelector(
    value: Float,
    onValueChange: (Float) -> Unit,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    shape: Shape = RoundedCornerShape(24.dp),
    title: String = stringResource(R.string.paint_alpha),
    icon: ImageVector = Icons.Rounded.Opacity
) {
    EnhancedSliderItem(
        modifier = modifier,
        value = value,
        icon = icon,
        title = title,
        sliderModifier = Modifier
            .padding(top = 14.dp, start = 12.dp, end = 12.dp, bottom = 10.dp),
        valueRange = 0f..1f,
        internalStateTransformation = {
            it.roundToTwoDigits()
        },
        onValueChange = {
            onValueChange(it.roundToTwoDigits())
        },
        shape = shape,
        color = color
    )
}