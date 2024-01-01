package dev.tberghuis.voicememos.tmp2

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TmpRecordingScreen(
  vm: TmpRecordingVm = viewModel()
) {

  val context = LocalContext.current

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

  val recordPermissionState = multiplePermissionsState.permissions.find {
    it.permission == android.Manifest.permission.RECORD_AUDIO
  }!!

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("tmp recording screen")
    if (recordPermissionState.status == PermissionStatus.Denied(false)) {
      // doitwrong
      // this is also displayed if user dismisses permission prompt on app start
      Text("please enable Microphone permission in system settings")
      Button(onClick = {
        launchPermissionsSettings(context)
      }) {
        Text("system settings")
      }
    } else { // microphone permission granted || shouldShowRationale == true
      Button(onClick = {
        if (recordPermissionState.status is PermissionStatus.Denied) {
          recordPermissionState.launchPermissionRequest()
        } else {
          vm.startRecording()
        }
      }) {
        Text("start")
      }
    }
  }
}


private fun launchPermissionsSettings(context: Context) {
  val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
  val uri = Uri.fromParts("package", context.packageName, null)
  intent.setData(uri)
  context.startActivity(intent)
}


