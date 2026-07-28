package dev.tberghuis.voicememos.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Text
import androidx.wear.compose.material3.SwitchButton

@Composable
fun SettingsPage() {
  // doitwrong
  // look at google samples for settings screen
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // todo settings cog icon as heading
    Text("Settings")
    SwitchButton(
      checked = false,
      onCheckedChange = {},
      modifier = Modifier,
      enabled = true,
    ) {
      Text("override back button to toggle recording")
    }
  }
}