package dev.tberghuis.voicememos.tmp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TmpScreen(
  vm: TmpVm = viewModel()
) {
  Column {
    Text("TmpScreen ${vm.tmp}")
  }
}