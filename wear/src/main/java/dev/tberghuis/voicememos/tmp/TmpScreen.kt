package dev.tberghuis.voicememos.tmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text

@Composable
fun TmpScreen(
  vm: TmpVm = viewModel()
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row {
      Button(onClick = {
        vm.tmpStartRecording()
      }) {
        Text("start")
      }
      Button(onClick = {
        vm.tmpGetCount()
      }) {
        Text("count")
      }
      Button(onClick = {
        vm.tmpStopRecording()
      }) {
        Text("stop")
      }
    }
    Row {
      Button(onClick = {
        vm.unbind()
      }) {
        Text("unbind")
      }
    }


  }
}
