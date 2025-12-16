package com.example.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.utils.PreferencesManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    prefs : PreferencesManager,
    modifier: Modifier,
    onChangeTheme: (Boolean) -> Unit
) {
    var isDarkModeToggled by rememberSaveable { mutableStateOf(prefs.getTheme()) }
    var is24HourToggled by rememberSaveable { mutableStateOf(prefs.get24HourFormat()) }

    Box(modifier
    ) {
        Column(Modifier
            .fillMaxHeight()
            .fillMaxWidth(1f)
            .padding(20.dp)
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Modo oscuro")
                Switch(
                    checked = isDarkModeToggled,
                    onCheckedChange = {
                        newState -> isDarkModeToggled = newState
                        onChangeTheme(newState)
                        prefs.saveTheme(isDarkModeToggled)
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Formato de 24 horas")
                Switch(
                    checked = is24HourToggled,
                    onCheckedChange = {
                            newState -> is24HourToggled = newState
                        prefs.save24HourFormat(is24HourToggled)
                    }
                )
            }

        }

    }

}
