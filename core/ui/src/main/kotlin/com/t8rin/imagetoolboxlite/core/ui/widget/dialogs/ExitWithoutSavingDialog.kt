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

package com.t8rin.imagetoolboxlite.core.ui.widget.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.t8rin.imagetoolboxlite.core.resources.R
import com.t8rin.imagetoolboxlite.core.ui.widget.buttons.EnhancedButton
import com.t8rin.imagetoolboxlite.core.ui.widget.modifier.alertDialogBorder

@Composable
fun ExitWithoutSavingDialog(
    onExit: () -> Unit,
    onDismiss: () -> Unit,
    visible: Boolean,
    text: String = stringResource(R.string.image_not_saved_sub),
    title: String = stringResource(R.string.image_not_saved),
    icon: ImageVector = Icons.Outlined.Save
) {
    if (visible) {
        AlertDialog(
            modifier = Modifier.alertDialogBorder(),
            onDismissRequest = onDismiss,
            dismissButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        onDismiss()
                        onExit()
                    }
                ) {
                    Text(stringResource(R.string.exit))
                }
            },
            confirmButton = {
                EnhancedButton(
                    onClick = onDismiss
                ) {
                    Text(stringResource(R.string.stay))
                }
            },
            title = { Text(text = title) },
            text = {
                Text(
                    text = text,
                    textAlign = TextAlign.Center
                )
            },
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        )
    }
}