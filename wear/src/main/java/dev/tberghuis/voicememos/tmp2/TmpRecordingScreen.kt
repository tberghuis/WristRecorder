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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import dev.tberghuis.voicememos.tmp.TmpVm

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TmpRecordingScreen(
  vm: TmpVm = viewModel()
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

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier.fillMaxSize(0.8f),
      verticalArrangement = Arrangement.SpaceAround,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
//    Text("tmp recording screen")
      if (recordPermissionState.status == PermissionStatus.Denied(false)) {
        // doitwrong
        // this is also displayed if user dismisses permission prompt on app start
        Text(
          "Please enable Microphone permission in settings",
          textAlign = TextAlign.Center,
        )
        Button(
          onClick = {
            launchPermissionsSettings(context)
          },
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text("Show Settings")
        }
      } else { // microphone permission granted || shouldShowRationale == true
        Button(onClick = {
          if (recordPermissionState.status is PermissionStatus.Denied) {
            recordPermissionState.launchPermissionRequest()
          } else {
            vm.tmpStartRecording()
          }
        }) {
          Text("start")
        }

        // todo merge into record button
        Button(onClick = { vm.tmpStopRecording() }) {
          Text("stop")
        }

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


