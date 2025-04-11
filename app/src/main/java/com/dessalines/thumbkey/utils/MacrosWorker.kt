package com.dessalines.thumbkey.utils

import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.ThumbkeyApplication
import com.dessalines.thumbkey.db.AppSettingsViewModel
import kotlinx.serialization.json.Json


fun userMacrosProcessor(ime: IMEService) {

    val repo = AppSettingsViewModel((ime.application as ThumbkeyApplication).appSettingsRepository).appSettings.value

    val macroses = Json.decodeFromString<List<Pair<String, String>>>(repo?.macrosList.toString())

    Log.d(TAG, "userMacrosProcessor")
    Log.d(TAG, "size of macros list is ${macroses}")

    for ((key, value) in macroses) {
        val textBefore = ime.currentInputConnection.getTextBeforeCursor(key.length, 0)
        if (!textBefore.isNullOrEmpty()) {
            if (textBefore == key) {
                ime.currentInputConnection.deleteSurroundingText(key.length, 0)
                ime.currentInputConnection.commitText(
                    value,
                    1,
                )
                break
            }
        }
    }
}