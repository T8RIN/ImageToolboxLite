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

package com.t8rin.imagetoolboxlite.core.ui.icons.material

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Filled.Prefix: ImageVector by lazy {
    Builder(
        name = "Prefix", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.14f, 4.0f)
            lineTo(6.43f, 16.0f)
            horizontalLineTo(8.36f)
            lineTo(9.32f, 13.43f)
            horizontalLineTo(14.67f)
            lineTo(15.64f, 16.0f)
            horizontalLineTo(17.57f)
            lineTo(12.86f, 4.0f)
            moveTo(12.0f, 6.29f)
            lineTo(14.03f, 11.71f)
            horizontalLineTo(9.96f)
            moveTo(4.0f, 18.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(2.0f)
            verticalLineTo(20.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(18.0f)
            close()
        }
    }.build()
}
