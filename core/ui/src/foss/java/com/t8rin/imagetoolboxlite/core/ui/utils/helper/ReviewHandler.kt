package com.t8rin.imagetoolboxlite.core.ui.utils.helper

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.t8rin.imagetoolboxlite.core.ui.utils.helper.ContextUtils.findActivity

object ReviewHandler {
    private val Context.dataStore by preferencesDataStore("saves_count")
    private val SAVES_COUNT = intPreferencesKey("SAVES_COUNT")
    private val NOT_SHOW_AGAIN = booleanPreferencesKey("NOT_SHOW_AGAIN")

    private val _showNotShowAgainButton = mutableStateOf(false)
    val showNotShowAgainButton: Boolean by _showNotShowAgainButton

    fun showReview(
        context: Context,
        onComplete: () -> Unit = {}
    ) {
        val activity = context.findActivity() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                if (it[NOT_SHOW_AGAIN] != true) {
                    val saves = it[SAVES_COUNT] ?: 0
                    it[SAVES_COUNT] = saves + 1

                    _showNotShowAgainButton.value = saves >= 30

                    if (saves % 10 == 0) {
                        activity.startActivity(
                            Intent(activity, activity::class.java).apply {
                                action = Intent.ACTION_BUG_REPORT
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                        )
                    }
                } else {
                    _showNotShowAgainButton.value = false
                }
            }
            onComplete()
        }
    }

    fun notShowReviewAgain(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                it[NOT_SHOW_AGAIN] = true
            }
        }
    }
}