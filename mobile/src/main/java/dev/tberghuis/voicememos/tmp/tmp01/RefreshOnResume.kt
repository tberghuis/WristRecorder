package dev.tberghuis.voicememos.tmp.tmp01

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RefreshOnResume(vm: MobileViewModel = viewModel()) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

  LaunchedEffect(lifecycleState) {
    when (lifecycleState) {
      Lifecycle.State.DESTROYED -> {}
      Lifecycle.State.INITIALIZED -> {}
      Lifecycle.State.CREATED -> {}
      Lifecycle.State.STARTED -> {}
      Lifecycle.State.RESUMED -> {
        vm.refreshRecordingFiles()
      }
    }
  }
}