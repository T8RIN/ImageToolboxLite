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

package com.t8rin.imagetoolboxlite.core.settings.domain.model

import com.t8rin.imagetoolboxlite.core.domain.Domain

sealed class FontFam(val ordinal: Int) : Domain {
    data object Montserrat : FontFam(1)
    data object Comfortaa : FontFam(3)
    data object Handjet : FontFam(4)
    data object Nothing : FontFam(16)
    data object System : FontFam(0)

    companion object {
        fun fromOrdinal(int: Int?): FontFam = when (int) {
            1 -> Montserrat
            3 -> Comfortaa
            4 -> Handjet
            16 -> Nothing
            else -> System
        }
    }
}
