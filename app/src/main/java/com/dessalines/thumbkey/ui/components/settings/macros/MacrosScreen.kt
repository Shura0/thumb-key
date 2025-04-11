package com.dessalines.thumbkey.ui.components.settings.macros

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.AppSettingsViewModel
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import com.dessalines.thumbkey.utils.TAG
import com.dessalines.thumbkey.utils.macrosesFromDbIndexString
import com.dessalines.thumbkey.utils.updateMacros
import kotlinx.coroutines.flow.MutableStateFlow
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun MacrosScreen(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel
) {

    Log.d(TAG, "Got to macros activity")
    val scrollState = rememberScrollState()

    val settings by appSettingsViewModel.appSettings.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var macrosMap = macrosesFromDbIndexString(settings?.macrosList)

    val inputList = remember { MutableStateFlow(macrosMap) }
    val inputListState by remember { inputList }.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.macros),
                navController = navController,
            )
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .padding(padding)
                        .verticalScroll(scrollState)
                        .background(color = MaterialTheme.colorScheme.surface)
                        .imePadding(),
            ) {
                ProvidePreferenceTheme {
                    inputListState.forEachIndexed { index, pair ->
                        TwoInputFields(
                            index, pair,
                            onChange = {
                                key, value -> val newList = ArrayList(inputListState)
                                if ( key != "" && value != "") {
                                    newList[index] = Pair(key, value)
                                    inputList.value = newList
                                    updateMacros(appSettingsViewModel, newList)
                                }
                            },
                            onDelete = { idx ->
                                val newList = ArrayList(inputListState)
                                newList.removeAt(index)
                                inputList.value = newList
                               updateMacros(appSettingsViewModel, newList)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                        val newList = ArrayList(inputListState)
                        newList.add(Pair("", ""))
                        inputList.value = newList
                    }) {
                        Text(stringResource(R.string.add))
                    }
                }
            }
        }
    )
}

@Composable
fun TwoInputFields(
    index: Int,
    pair: Pair<String, String>,
    onChange: (String, String) -> Unit,

    onDelete: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var text1 = remember(pair.first) { mutableStateOf(pair.first) }
        var text2 = remember(pair.second) { mutableStateOf(pair.second) }

        TextField(
            value = text1.value,
            onValueChange = {
                text1.value = it
                onChange(text1.value, text2.value)

            },
            label = { Text(stringResource(R.string.macro)) },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        TextField(
            value = text2.value,
            onValueChange = {
                text2.value = it
                onChange(text1.value, text2.value)
            },
            label = { Text(stringResource(R.string.text)) },
            modifier = Modifier
                .weight(2f)
                .padding(end = 8.dp)
        )

        Button(onClick = {
            onDelete(index)
        },
            modifier = Modifier
                .weight(0.5f)
                .padding(0.dp)) {
            Text("-")
        }
    }
}