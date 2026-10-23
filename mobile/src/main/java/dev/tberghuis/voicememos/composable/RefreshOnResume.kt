package dev.tberghuis.voicememos.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.vm.MobileViewModel

@Composable
fun RefreshOnResume(vm: MobileViewModel = viewModel()) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

  LaunchedEffect(lifecycleState) {
    if (lifecycleState == Lifecycle.State.RESUMED) {
      vm.refreshRecordingFiles()
    }
  }
}