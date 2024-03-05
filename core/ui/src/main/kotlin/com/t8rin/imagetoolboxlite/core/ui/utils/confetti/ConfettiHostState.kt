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

package com.t8rin.imagetoolboxlite.core.ui.utils.confetti

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import com.t8rin.imagetoolboxlite.core.ui.widget.other.ToastDuration
import com.t8rin.imagetoolboxlite.core.ui.widget.other.ToastHost
import com.t8rin.imagetoolboxlite.core.ui.widget.other.ToastHostState

val LocalConfettiHostState = compositionLocalOf { ConfettiHostState() }

@Composable
fun rememberConfettiHostState() = remember { ConfettiHostState() }

class ConfettiHostState : ToastHostState() {
    suspend fun showConfetti(
        duration: ToastDuration = ToastDuration(4500L)
    ) = showToast(message = "", duration = duration)
}

@Composable
fun ConfettiHost(
    hostState: ConfettiHostState,
    particles: @Composable (primary: Color) -> List<Party>
) {
    ToastHost(
        hostState = hostState,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        toast = {
            val primary = MaterialTheme.colorScheme.primary
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = particles(primary)
            )
        }
    )
}