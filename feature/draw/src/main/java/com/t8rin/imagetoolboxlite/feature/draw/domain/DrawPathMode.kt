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

package com.t8rin.imagetoolboxlite.feature.draw.domain

sealed class DrawPathMode(open val ordinal: Int) {

    data object Free : DrawPathMode(-1)
    data object Line : DrawPathMode(0)
    data object PointingArrow : DrawPathMode(1)
    data object DoublePointingArrow : DrawPathMode(2)
    data object LinePointingArrow : DrawPathMode(3)
    data object DoubleLinePointingArrow : DrawPathMode(4)
    data object Lasso : DrawPathMode(5)
    data object OutlinedRect : DrawPathMode(6)
    data object OutlinedOval : DrawPathMode(7)
    data object Rect : DrawPathMode(8)
    data object Oval : DrawPathMode(9)

    companion object {
        val entries by lazy {
            listOf(
                Free,
                Line,
                PointingArrow,
                DoublePointingArrow,
                LinePointingArrow,
                DoubleLinePointingArrow,
                Lasso,
                OutlinedRect,
                OutlinedOval,
                Rect,
                Oval
            )
        }

        operator fun invoke(ordinal: Int) = entries.find {
            it.ordinal == ordinal
        } ?: Free
    }
}