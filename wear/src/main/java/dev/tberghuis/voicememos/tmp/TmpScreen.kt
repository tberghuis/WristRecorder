package dev.tberghuis.voicememos.tmp

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dev.tberghuis.voicememos.common.logd

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

      PermissionButton()

    }


  }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionButton(
  vm: TmpVm = viewModel()
) {
  // 33 = android 13
  val permissionState = if (Build.VERSION.SDK_INT >= 33) {
    rememberPermissionState(
      android.Manifest.permission.POST_NOTIFICATIONS
    )
  } else {
    TODO("VERSION.SDK_INT < TIRAMISU")
  }
  Button(onClick = {
    permissionState.launchPermissionRequest()
  }) {
    Text("permission")
  }
}
