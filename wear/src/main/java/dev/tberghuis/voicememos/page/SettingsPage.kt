package dev.tberghuis.voicememos.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Text
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.SwitchButton
import dev.tberghuis.voicememos.viewmodels.SettingsVm

@Composable
fun SettingsPage(
  vm: SettingsVm = viewModel()
) {
  val backOverride by vm.backOverride.collectAsState()
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth(0.8f)
        .fillMaxHeight(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = "Settings",
        modifier = Modifier.padding(bottom = 8.dp),
      )
      SwitchButton(
        checked = backOverride,
        onCheckedChange = { vm.toggleBackOverride() },
        modifier = Modifier,
        enabled = true,
      ) {
        Text("override back button to toggle recording")
      }
    }
  }
}