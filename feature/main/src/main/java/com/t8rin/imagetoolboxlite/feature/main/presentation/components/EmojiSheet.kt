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

package com.t8rin.imagetoolboxlite.feature.main.presentation.components

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face5
import androidx.compose.material.icons.outlined.Face6
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.icons.emoji.EmojiData
import com.t8rin.imagetoolboxlite.core.ui.shapes.CloverShape
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedButton
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedIconButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.container
import com.t8rin.imagetoolboxlite.core.ui.widget.other.EmojiItem
import com.t8rin.imagetoolboxlite.core.ui.widget.other.GradientEdge
import com.t8rin.imagetoolboxlite.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolboxlite.core.ui.widget.sheets.SimpleSheet
import com.t8rin.imagetoolboxlite.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolboxlite.core.ui.widget.text.TitleItem
import kotlin.random.Random

@Composable
fun EmojiSheet(
    selectedEmojiIndex: Int,
    emojiWithCategories: ImmutableList<EmojiData>,
    allEmojis: ImmutableList<Uri>,
    onEmojiPicked: (Int) -> Unit,
    visible: MutableState<Boolean>
) {
    var showSheet by visible
    val state = rememberLazyGridState()

    LaunchedEffect(showSheet) {
        delay(200)
        if (selectedEmojiIndex >= 0) {
            state.animateScrollToItem(selectedEmojiIndex)
        }
    }

    val emojiEnabled by remember(selectedEmojiIndex) {
        derivedStateOf {
            selectedEmojiIndex != -1
        }
    }
    val scope = rememberCoroutineScope()

    SimpleSheet(
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnhancedIconButton(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    enabled = emojiEnabled,
                    onClick = {
                        onEmojiPicked(Random.nextInt(0, allEmojis.lastIndex))
                        scope.launch {
                            state.animateScrollToItem(selectedEmojiIndex)
                        }
                    },
                ) {
                    Icon(Icons.Rounded.Shuffle, null)
                }
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { showSheet = false }
                ) {
                    AutoSizeText(stringResource(R.string.close))
                }
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.emoji),
                icon = Icons.Outlined.Face5
            )
        },
        visible = visible
    ) {
        val alphaState by remember(emojiEnabled) {
            derivedStateOf {
                if (emojiEnabled) 1f else 0.4f
            }
        }
        val spanModifier = Modifier
            .padding(vertical = 16.dp)
            .container(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    0.4f
                ),
                resultPadding = 0.dp
            )
            .padding(16.dp)
        Box {
            val density = LocalDensity.current
            var topPadding by remember {
                mutableStateOf(0.dp)
            }
            val contentPadding by remember(topPadding) {
                derivedStateOf {
                    PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                        top = topPadding
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyVerticalGrid(
                    state = state,
                    columns = GridCells.Adaptive(55.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .alpha(
                            animateFloatAsState(alphaState).value
                        ),
                    userScrollEnabled = emojiEnabled,
                    verticalArrangement = Arrangement.spacedBy(
                        6.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        6.dp,
                        Alignment.CenterHorizontally
                    ),
                    contentPadding = contentPadding
                ) {
                    emojiWithCategories.forEach { (title, icon, emojis) ->
                        item(
                            span = { GridItemSpan(maxLineSpan) },
                            key = icon.name
                        ) {
                            Row(
                                modifier = spanModifier,
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(icon, null, modifier = Modifier.padding(end = 16.dp))
                                Text(title)
                            }
                        }
                        emojis.forEach { emoji ->
                            item(
                                key = emoji
                            ) {
                                val index by remember(allEmojis, emoji) {
                                    derivedStateOf {
                                        allEmojis.indexOf(emoji)
                                    }
                                }
                                val selected by remember(index, selectedEmojiIndex) {
                                    derivedStateOf {
                                        index == selectedEmojiIndex
                                    }
                                }
                                val color by animateColorAsState(
                                    if (selected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = 0.2f
                                    )
                                )
                                val borderColor by animateColorAsState(
                                    if (selected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                                    } else MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.1f
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .container(
                                            color = color,
                                            shape = CloverShape,
                                            borderColor = borderColor,
                                            resultPadding = 0.dp
                                        )
                                        .clickable {
                                            onEmojiPicked(index)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    EmojiItem(
                                        emoji = emoji.toString(),
                                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                        fontScale = 1f,
                                        isFullQuality = false
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.onGloballyPositioned {
                    topPadding = with(density) {
                        it.size.height.toDp()
                    }
                }
            ) {
                PreferenceRowSwitch(
                    title = stringResource(R.string.enable_emoji),
                    color = animateColorAsState(
                        if (emojiEnabled) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceContainerLow
                    ).value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                        .padding(start = 16.dp, top = 20.dp, bottom = 8.dp, end = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    checked = emojiEnabled,
                    startIcon = Icons.Outlined.Face6,
                    onClick = {
                        if (!emojiEnabled) onEmojiPicked(Random.nextInt(0, allEmojis.lastIndex))
                        else onEmojiPicked(-1)
                    }
                )
                GradientEdge(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    startColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    endColor = Color.Transparent
                )
            }
        }
    }
}