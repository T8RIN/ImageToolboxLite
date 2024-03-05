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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolboxlite.core.domain.APP_LINK
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.alertDialogBorder

@Composable
fun GithubReviewDialog(
    onDismiss: () -> Unit,
    showNotShowAgainButton: Boolean,
    onNotShowAgain: () -> Unit
) {
    val linkHandler = LocalUriHandler.current
    AlertDialog(
        modifier = Modifier.alertDialogBorder(),
        onDismissRequest = onDismiss,
        confirmButton = {
            EnhancedButton(
                onClick = {
                    linkHandler.openUri(APP_LINK)
                }
            ) {
                Text(text = stringResource(id = R.string.rate))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    if (showNotShowAgainButton) onNotShowAgain()

                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.close))
            }
        },
        title = {
            Text(stringResource(R.string.rate_app))
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.StarRate,
                contentDescription = null
            )
        },
        text = {
            Text(stringResource(R.string.rate_app_sub))
        }
    )
}