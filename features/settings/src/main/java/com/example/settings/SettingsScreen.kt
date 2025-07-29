package com.example.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
    padding: PaddingValues,
    onChangeTheme: (Boolean) -> Unit
) {
    var isToggled by rememberSaveable { mutableStateOf(prefs.GetTheme()) }



    Box(
        Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(Modifier
            .fillMaxSize()
            .padding(20.dp)
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Modo oscuro")
                Switch(
                    checked = isToggled,
                    onCheckedChange = {
                        newState -> isToggled = newState
                        onChangeTheme(newState)
                        prefs.SaveTheme(isToggled)
                    }
                )
            }

        }

    }

}
