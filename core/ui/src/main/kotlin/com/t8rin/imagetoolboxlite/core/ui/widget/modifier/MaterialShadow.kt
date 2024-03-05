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

package com.t8rin.imagetoolboxlite.core.ui.widget.modifier

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import com.zedalpha.shadowgadgets.compose.clippedShadow

@Composable
fun Modifier.materialShadow(
    shape: Shape,
    elevation: Dp,
    enabled: Boolean = true,
    isClipped: Boolean = true,
    color: Color = Color.Black
) = this.composed {
    val isConcavePath by remember(shape) {
        derivedStateOf {
            shape.createOutline(
                size = Size(1f, 1f),
                layoutDirection = LayoutDirection.Ltr,
                density = Density(1f)
            ).let {
                it is Outline.Generic && !it.path.isConvex
            }
        }
    }
    val elev = animateDpAsState(if (enabled) elevation else 0.dp).value

    val api29Shadow = if (isClipped) {
        Modifier.clippedShadow(
            shape = shape,
            elevation = elev,
            ambientColor = color,
            spotColor = color
        )
    } else {
        Modifier.shadow(
            shape = shape,
            elevation = elev,
            ambientColor = color,
            spotColor = color
        )
    }

    val api21shadow = Modifier.rsBlurShadow(
        shape = shape,
        radius = elev,
        isAlphaContentClip = isClipped,
        color = color
    )
    when {
        isConcavePath && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> api21shadow
        else -> api29Shadow
    }
}