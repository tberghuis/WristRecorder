package dev.tberghuis.voicememos.tmp2

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TmpRecordingScreen(
  vm: TmpRecordingVm = viewModel()
) {

  // 33 = android 13
  val permissionsList = if (Build.VERSION.SDK_INT >= 33) {
    listOf(
      android.Manifest.permission.RECORD_AUDIO,
      android.Manifest.permission.POST_NOTIFICATIONS,
    )
  } else {
    listOf(
      android.Manifest.permission.RECORD_AUDIO,
    )
  }
  val multiplePermissionsState = rememberMultiplePermissionsState(
    permissionsList
  )

  if (!multiplePermissionsState.allPermissionsGranted) {
    LaunchedEffect(Unit) {
      multiplePermissionsState.launchMultiplePermissionRequest()
    }
  }



  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("tmp recording screen")
    Button(onClick = { vm.startRecording() }) {
      Text("start")
    }
  }
}
