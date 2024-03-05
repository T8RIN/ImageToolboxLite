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

package com.t8rin.imagetoolboxlite.feature.filters.data

import android.content.Context
import android.graphics.Bitmap
import com.t8rin.imagetoolboxlite.core.domain.image.Transformation
import com.t8rin.imagetoolboxlite.core.filters.domain.FilterProvider
import com.t8rin.imagetoolboxlite.core.filters.domain.model.Filter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.AnaglyphFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.AtkinsonDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.BayerEightDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.BayerFourDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.BayerThreeDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.BayerTwoDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.BlackAndWhiteFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.BulgeDistortionFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.BurkesDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.CGAColorSpaceFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.CirclePixelationFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.ColorBalanceFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.ColorFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.ColorMatrix4x4Filter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.CrosshatchFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.DiamondPixelationFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.EnhancedCirclePixelationFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.EnhancedDiamondPixelationFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.EnhancedPixelationFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.ExposureFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.FalseColorFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.FalseFloydSteinbergDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.FastBlurFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.FloydSteinbergDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.GlassSphereRefractionFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.GlitchFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.HalftoneFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.HazeFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.HighlightsAndShadowsFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.HueFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.JarvisJudiceNinkeDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.KuwaharaFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.LaplacianFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.LeftToRightDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.LookupFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.NegativeFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.NeonFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.NoiseFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.NonMaximumSuppressionFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.OpacityFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.PixelationFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.PosterizeFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.RGBFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.RandomDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.RemoveColorFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.ReplaceColorFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.ShuffleBlurFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.SideFadeFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.SierraDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.SierraLiteDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.SimpleThresholdDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.SmoothToonFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.SobelEdgeDetectionFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.SolarizeFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.SphereRefractionFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.StackBlurFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.StrokePixelationFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.StuckiDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.SwirlDistortionFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.ToonFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.TwoRowSierraDitheringFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.VignetteFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.WeakPixelFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.WhiteBalanceFilter
import com.t8rin.imagetoolboxlite.feature.filters.data.model.ZoomBlurFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class AndroidFilterProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : FilterProvider<Bitmap> {

    override fun filterToTransformation(
        filter: Filter<Bitmap, *>
    ): Transformation<Bitmap> = filter.run {
        when (this) {
            is Filter.BlackAndWhite -> BlackAndWhiteFilter(context)
            is Filter.BulgeDistortion -> BulgeDistortionFilter(context, value)
            is Filter.CGAColorSpace -> CGAColorSpaceFilter(context)
            is Filter.CirclePixelation -> CirclePixelationFilter(value)
            is Filter.ColorBalance -> ColorBalanceFilter(context, value)
            is Filter.Color<*, *> -> ColorFilter(value.cast())
            is Filter.ColorMatrix4x4 -> ColorMatrix4x4Filter(context, value)
            is Filter.Crosshatch -> CrosshatchFilter(context, value)
            is Filter.DiamondPixelation -> DiamondPixelationFilter(value)
            is Filter.EnhancedCirclePixelation -> EnhancedCirclePixelationFilter(value)
            is Filter.EnhancedDiamondPixelation -> EnhancedDiamondPixelationFilter(value)
            is Filter.EnhancedPixelation -> EnhancedPixelationFilter(value)
            is Filter.Exposure -> ExposureFilter(context, value)
            is Filter.FalseColor<*, *> -> FalseColorFilter(context, value.cast())
            is Filter.FastBlur -> FastBlurFilter(value)
            is Filter.GlassSphereRefraction -> GlassSphereRefractionFilter(context, value)
            is Filter.Halftone -> HalftoneFilter(context, value)
            is Filter.Haze -> HazeFilter(context, value)
            is Filter.HighlightsAndShadows -> HighlightsAndShadowsFilter(context, value)
            is Filter.Hue -> HueFilter(context, value)
            is Filter.Kuwahara -> KuwaharaFilter(context, value)
            is Filter.Laplacian -> LaplacianFilter(context)
            is Filter.Lookup -> LookupFilter(context, value)
            is Filter.Negative -> NegativeFilter(context)
            is Filter.NonMaximumSuppression -> NonMaximumSuppressionFilter(context)
            is Filter.Opacity -> OpacityFilter(context, value)
            is Filter.Pixelation -> PixelationFilter(value)
            is Filter.Posterize -> PosterizeFilter(context, value)
            is Filter.RemoveColor<*, *> -> RemoveColorFilter(value.cast())
            is Filter.ReplaceColor<*, *> -> ReplaceColorFilter(value.cast())
            is Filter.RGB<*, *> -> RGBFilter(context, value.cast())
            is Filter.SmoothToon -> SmoothToonFilter(context, value)
            is Filter.SobelEdgeDetection -> SobelEdgeDetectionFilter(context, value)
            is Filter.Solarize -> SolarizeFilter(context, value)
            is Filter.SphereRefraction -> SphereRefractionFilter(context, value)
            is Filter.StackBlur -> StackBlurFilter(value)
            is Filter.StrokePixelation<*, *> -> StrokePixelationFilter(value.cast())
            is Filter.SwirlDistortion -> SwirlDistortionFilter(context, value)
            is Filter.Toon -> ToonFilter(context, value)
            is Filter.Vignette<*, *> -> VignetteFilter(context, value.cast())
            is Filter.WeakPixel -> WeakPixelFilter(context)
            is Filter.WhiteBalance -> WhiteBalanceFilter(context, value)
            is Filter.ZoomBlur -> ZoomBlurFilter(context, value)
            is Filter.BayerTwoDithering -> BayerTwoDitheringFilter(value)
            is Filter.BayerThreeDithering -> BayerThreeDitheringFilter(value)
            is Filter.BayerFourDithering -> BayerFourDitheringFilter(value)
            is Filter.BayerEightDithering -> BayerEightDitheringFilter(value)
            is Filter.FloydSteinbergDithering -> FloydSteinbergDitheringFilter(value)
            is Filter.JarvisJudiceNinkeDithering -> JarvisJudiceNinkeDitheringFilter(value)
            is Filter.SierraDithering -> SierraDitheringFilter(value)
            is Filter.TwoRowSierraDithering -> TwoRowSierraDitheringFilter(value)
            is Filter.SierraLiteDithering -> SierraLiteDitheringFilter(value)
            is Filter.AtkinsonDithering -> AtkinsonDitheringFilter(value)
            is Filter.StuckiDithering -> StuckiDitheringFilter(value)
            is Filter.BurkesDithering -> BurkesDitheringFilter(value)
            is Filter.FalseFloydSteinbergDithering -> FalseFloydSteinbergDitheringFilter(value)
            is Filter.LeftToRightDithering -> LeftToRightDitheringFilter(value)
            is Filter.RandomDithering -> RandomDitheringFilter(value)
            is Filter.SimpleThresholdDithering -> SimpleThresholdDitheringFilter(value)
            is Filter.Glitch -> GlitchFilter(value)
            is Filter.Anaglyph -> AnaglyphFilter(value)
            is Filter.Noise -> NoiseFilter(value)
            is Filter.SideFade -> SideFadeFilter(value)
            is Filter.Neon<*, *> -> NeonFilter(context, value.cast())
            is Filter.ShuffleBlur -> ShuffleBlurFilter(value)

            else -> throw IllegalArgumentException("No filter implementation for interface ${filter::class.simpleName}")
        }
    }

}

private inline fun <reified T, reified R> T.cast(): R = this as R