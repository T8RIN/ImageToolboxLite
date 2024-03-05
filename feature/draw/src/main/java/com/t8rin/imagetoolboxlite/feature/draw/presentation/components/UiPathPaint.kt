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

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.t8rin.imagetoolboxlite.core.domain.model.IntegerSize
import com.t8rin.imagetoolboxlite.feature.draw.domain.DrawMode
import com.t8rin.imagetoolboxlite.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolboxlite.feature.draw.domain.PathPaint
import com.t8rin.imagetoolboxlite.feature.draw.domain.Pt
import com.t8rin.imagetoolboxlite.feature.draw.domain.pt

data class UiPathPaint(
    override val path: Path,
    override val strokeWidth: Pt,
    override val brushSoftness: Pt,
    override val drawColor: Color = Color.Transparent,
    override val isErasing: Boolean,
    override val drawMode: DrawMode = DrawMode.Pen,
    override val canvasSize: IntegerSize,
    override val drawPathMode: DrawPathMode = DrawPathMode.Free
) : PathPaint<Path, Color>


fun PathPaint<Path, Color>.toUiPathPaint() = UiPathPaint(
    path = path,
    strokeWidth = strokeWidth,
    brushSoftness = brushSoftness,
    drawColor = drawColor,
    isErasing = isErasing,
    drawMode = drawMode,
    canvasSize = canvasSize,
    drawPathMode = drawPathMode
)

val PtSaver: Saver<Pt, Float> = Saver(
    save = {
        it.value
    },
    restore = {
        it.pt
    }
)