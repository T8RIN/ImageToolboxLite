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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.delay

fun Modifier.scaleOnTap(
    initial: Float = 1f,
    min: Float = 0.8f,
    max: Float = 1.3f,
    onHold: () -> Unit = {},
    onRelease: (time: Long) -> Unit
) = this.composed {
    var scaleState by remember(initial) { mutableFloatStateOf(initial) }
    val scale by animateFloatAsState(scaleState)
    val haptics = LocalHapticFeedback.current

    Modifier
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    val time = System.currentTimeMillis()
                    scaleState = max
                    haptics.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
                    onHold()
                    delay(200)
                    tryAwaitRelease()
                    onRelease(System.currentTimeMillis() - time)
                    haptics.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
                    scaleState = min
                    delay(200)
                    scaleState = initial
                }
            )
        }
}