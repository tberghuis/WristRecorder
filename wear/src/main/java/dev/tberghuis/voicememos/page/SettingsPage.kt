package dev.tberghuis.voicememos.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Text
import androidx.wear.compose.material3.SwitchButton
import dev.tberghuis.voicememos.viewmodels.SettingsVm

@Composable
fun SettingsPage(
  vm: SettingsVm = viewModel()
) {

  val backOverride by vm.backOverride.collectAsState()

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
      checked = backOverride,
      onCheckedChange = { vm.toggleBackOverride() },
      modifier = Modifier,
      enabled = true,
    ) {
      Text("override back button to toggle recording")
    }
  }
}