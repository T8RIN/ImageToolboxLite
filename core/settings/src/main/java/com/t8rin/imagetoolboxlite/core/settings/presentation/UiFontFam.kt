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

@file:Suppress("MemberVisibilityCanBePrivate")

package com.t8rin.imagetoolboxlite.core.settings.presentation

import android.os.Build
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.settings.domain.model.FontFam

sealed class UiFontFam(
    val name: String?,
    private val variable: Boolean,
    val fontRes: Int?
) {
    val isVariable: Boolean?
        get() = variable.takeIf {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

    val fontFamily: FontFamily
        get() = fontRes?.let {
            fontFamilyResource(resId = fontRes)
        } ?: FontFamily.Default

    operator fun component1() = fontFamily
    operator fun component2() = name
    operator fun component3() = isVariable
    operator fun component4() = fontRes

    data object Montserrat : UiFontFam(
        fontRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.montserrat_variable
        } else R.font.montserrat_regular,
        name = "Montserrat",
        variable = true
    )

    data object System : UiFontFam(
        fontRes = null,
        name = null,
        variable = true
    )

    data object Comfortaa : UiFontFam(
        fontRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.font.comfortaa_varibale
        } else R.font.comfortaa_regular,
        name = "Comfortaa",
        variable = true
    )

    data object Handjet : UiFontFam(
        fontRes = R.font.handjet_varibale,
        name = "Handjet",
        variable = true
    )

    data object Nothing : UiFontFam(
        fontRes = R.font.nothing_font_regular,
        name = "Nothing",
        variable = false
    )

    fun asDomain(): FontFam {
        return when (this) {
            Comfortaa -> FontFam.Comfortaa
            System -> FontFam.System
            Handjet -> FontFam.Handjet
            Montserrat -> FontFam.Montserrat
            Nothing -> FontFam.Nothing
        }
    }

    companion object {
        val entries by lazy {
            listOf(
                Montserrat,
                Comfortaa,
                Handjet,
                Nothing,
                System
            ).sortedBy { it.name }
        }
    }
}

fun FontFam.toUiFont(): UiFontFam {
    return when (this) {
        FontFam.Comfortaa -> UiFontFam.Comfortaa
        FontFam.System -> UiFontFam.System
        FontFam.Handjet -> UiFontFam.Handjet
        FontFam.Montserrat -> UiFontFam.Montserrat
        FontFam.Nothing -> UiFontFam.Nothing
    }
}

@OptIn(ExperimentalTextApi::class)
private fun fontFamilyResource(resId: Int) = FontFamily(
    Font(
        resId = resId,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Light,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = resId,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = resId,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = resId,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal
        )
    ),
    Font(
        resId = resId,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
)