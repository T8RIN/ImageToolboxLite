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

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import com.t8rin.logger.makeLog
import com.t8rin.imagetoolboxlite.core.ui.utils.state.update


/**
Modifier which helps you to implement google photos selection grid,
to make it work pass [key] and list item key which is should be "[key]-position"
 **/
fun Modifier.dragHandler(
    key: Any?,
    isVertical: Boolean,
    lazyGridState: LazyGridState,
    haptics: HapticFeedback,
    selectedItems: MutableState<Set<Int>>,
    onSelectionChange: (Set<Int>) -> Unit = {},
    autoScrollSpeed: MutableState<Float>,
    autoScrollThreshold: Float
): Modifier {
    fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? {
        val find = layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }
        val itemKey = find?.key
        return itemKey?.toString()?.takeLastWhile { it != '-' }?.toIntOrNull()
    }

    return this
        .pointerInput(key) {
            detectTapGestures { offset ->
                lazyGridState
                    .gridItemKeyAtPosition(offset)
                    .makeLog("Logger_TAP")
                    ?.let { key ->
                        val newItems = if (selectedItems.value.contains(key)) {
                            selectedItems.value - key
                        } else {
                            selectedItems.value + key
                        }
                        selectedItems.update { newItems }
                        onSelectionChange(newItems)
                    }
            }
        }
        .pointerInput(key) {
            var initialKey: Int? = null
            var currentKey: Int? = null
            detectDragGesturesAfterLongPress(
                onDragStart = { offset ->
                    lazyGridState
                        .gridItemKeyAtPosition(offset)
                        .makeLog("Logger_START")
                        ?.let { key ->
                            if (!selectedItems.value.contains(key)) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                initialKey = key
                                currentKey = key
                                val newItems = selectedItems.value + key
                                selectedItems.update { newItems }
                                onSelectionChange(newItems)
                            }
                        }
                },
                onDragCancel = {
                    initialKey = null
                    autoScrollSpeed.value = 0f
                },
                onDragEnd = {
                    initialKey = null
                    autoScrollSpeed.value = 0f
                },
                onDrag = { change, _ ->
                    if (initialKey != null) {
                        val distFromBottom = if (isVertical) {
                            lazyGridState.layoutInfo.viewportSize.height - change.position.y
                        } else lazyGridState.layoutInfo.viewportSize.width - change.position.x
                        val distFromTop = if (isVertical) {
                            change.position.y
                        } else change.position.x
                        autoScrollSpeed.value = when {
                            distFromBottom < autoScrollThreshold -> autoScrollThreshold - distFromBottom
                            distFromTop < autoScrollThreshold -> -(autoScrollThreshold - distFromTop)
                            else -> 0f
                        }

                        lazyGridState
                            .gridItemKeyAtPosition(change.position)
                            .makeLog("Logger_DRAG")
                            ?.let { key ->
                                if (currentKey != key) {
                                    val newItems = selectedItems.value
                                        .minus(initialKey!!..currentKey!!)
                                        .minus(currentKey!!..initialKey!!)
                                        .plus(initialKey!!..key)
                                        .plus(key..initialKey!!)

                                    selectedItems.update { newItems }
                                    onSelectionChange(newItems)
                                    currentKey = key
                                }
                            }
                    }
                }
            )
        }
}