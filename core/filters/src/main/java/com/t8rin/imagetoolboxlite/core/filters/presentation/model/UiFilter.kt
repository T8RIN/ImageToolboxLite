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

package com.t8rin.imagetoolboxlite.core.filters.presentation.model

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.StringRes
import com.t8rin.imagetoolboxlite.core.filters.domain.model.Filter
import com.t8rin.imagetoolboxlite.core.filters.domain.model.FilterParam
import com.t8rin.logger.makeLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.full.primaryConstructor

sealed class UiFilter<T>(
    @StringRes val title: Int,
    val paramsInfo: List<FilterParam> = listOf(),
    override val value: T
) : Filter<Bitmap, T> {

    constructor(
        @StringRes title: Int,
        valueRange: ClosedFloatingPointRange<Float>,
        value: T,
    ) : this(
        title = title,
        paramsInfo = listOf(
            FilterParam(valueRange = valueRange)
        ),
        value = value
    )

    fun <T : Any> copy(value: T): UiFilter<*> {
        if (this.value == null) return newInstance()
        if (this.value!!::class.simpleName != value::class.simpleName) return newInstance()
        return runCatching {
            this::class.primaryConstructor?.run {
                callBy(mapOf(parameters[0] to value))
            }
        }.getOrNull() ?: newInstance()
    }

    fun newInstance(): UiFilter<*> = this::class.primaryConstructor!!.callBy(emptyMap())

    companion object {
        val groupedEntries by lazy {
            listOf(
                listOf(
                    UiNegativeFilter(),
                    UiBlackAndWhiteFilter(),
                    UiCGAColorSpaceFilter(),
                    UiLaplacianFilter(),
                    UiNonMaximumSuppressionFilter(),
                    UiWeakPixelFilter(),
                ),
                listOf(
                    UiHueFilter(),
                    UiColorFilter(),
                    UiNeonFilter(),
                    UiRGBFilter(),
                    UiReplaceColorFilter(),
                    UiRemoveColorFilter(),
                    UiFalseColorFilter(),
                    UiColorMatrix4x4Filter(),
                    UiColorBalanceFilter()
                ),
                listOf(
                    UiExposureFilter(),
                    UiWhiteBalanceFilter(),
                    UiHighlightsAndShadowsFilter(),
                    UiSolarizeFilter(),
                    UiHazeFilter()
                ),
                listOf(
                    UiSobelEdgeDetectionFilter(),
                    UiVignetteFilter(),
                    UiKuwaharaFilter(),
                    UiOpacityFilter(),
                    UiSideFadeFilter(),
                    UiToonFilter(),
                    UiSmoothToonFilter(),
                    UiPosterizeFilter(),
                    UiLookupFilter()
                ),
                listOf(
                    UiShuffleBlurFilter(),
                    UiStackBlurFilter(),
                    UiFastBlurFilter(),
                    UiZoomBlurFilter()
                ),
                listOf(
                    UiPixelationFilter(),
                    UiEnhancedPixelationFilter(),
                    UiDiamondPixelationFilter(),
                    UiEnhancedDiamondPixelationFilter(),
                    UiCirclePixelationFilter(),
                    UiEnhancedCirclePixelationFilter(),
                    UiStrokePixelationFilter()
                ),
                listOf(
                    UiGlitchFilter(),
                    UiAnaglyphFilter(),
                    UiNoiseFilter(),
                    UiBulgeDistortionFilter(),
                    UiSphereRefractionFilter(),
                    UiGlassSphereRefractionFilter()
                ),
                listOf(
                    UiHalftoneFilter(),
                    UiCrosshatchFilter(),
                    UiBayerTwoDitheringFilter(),
                    UiBayerThreeDitheringFilter(),
                    UiBayerFourDitheringFilter(),
                    UiBayerEightDitheringFilter(),
                    UiFloydSteinbergDitheringFilter(),
                    UiJarvisJudiceNinkeDitheringFilter(),
                    UiSierraDitheringFilter(),
                    UiTwoRowSierraDitheringFilter(),
                    UiSierraLiteDitheringFilter(),
                    UiAtkinsonDitheringFilter(),
                    UiStuckiDitheringFilter(),
                    UiBurkesDitheringFilter(),
                    UiFalseFloydSteinbergDitheringFilter(),
                    UiLeftToRightDitheringFilter(),
                    UiRandomDitheringFilter(),
                    UiSimpleThresholdDitheringFilter()
                )
            )
        }

        fun groupedEntries(
            context: Context
        ) = groupedEntries.map { list ->
            list.sortedBy { context.getString(it.title) }
        }
    }

}

private val sealedValues = UiFilter::class.sealedSubclasses

fun Filter<Bitmap, *>.toUiFilter(): UiFilter<*> = sealedValues.first {
    it.java.isAssignableFrom(this::class.java)
}.primaryConstructor!!.run {
    if (parameters.isNotEmpty()) callBy(mapOf(parameters[0] to value))
    else callBy(emptyMap())
}

infix fun Int.paramTo(valueRange: ClosedFloatingPointRange<Float>) = FilterParam(
    title = this,
    valueRange = valueRange
)

private suspend fun reflectionTest() = withContext(Dispatchers.IO) {
    val filters = UiFilter.groupedEntries.flatten()
    val failedCopy = mutableListOf<Pair<String, String?>>()
    val failedToUi = mutableListOf<Pair<String, String?>>()
    filters.forEach { filter ->
        runCatching {
            filter.copy(filter.value)
        }.onFailure {
            failedCopy.add(filter::class.simpleName.toString() to it.message)
        }
        runCatching {
            filter.toUiFilter()
        }.onFailure {
            failedToUi.add(filter::class.simpleName.toString() to it.message)
        }
    }
    "------------------".makeLog()
    failedCopy.makeLog()
    " ".makeLog()
    failedToUi.makeLog()
    "------------------".makeLog()
}