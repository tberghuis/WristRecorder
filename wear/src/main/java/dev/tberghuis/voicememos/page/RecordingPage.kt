package dev.tberghuis.voicememos.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.composables.RecordingUi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordingPage(
  navigateRecordingDetail: (String) -> Unit
) {
  val recordPermissionState = rememberPermissionState(
    android.Manifest.permission.RECORD_AUDIO
  )
  Column(
    Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    when (recordPermissionState.status) {
      PermissionStatus.Granted -> {
        RecordingUi(navigateRecordingDetail)
      }
      is PermissionStatus.Denied -> {
        LaunchedEffect(Unit) {
          logd("effect")
          recordPermissionState.launchPermissionRequest()
        }
        val shouldShowRationale = recordPermissionState.status.shouldShowRationale
        logd("shouldShowRationale $shouldShowRationale")

        if (shouldShowRationale) {
          Button(onClick = {
            logd("enable")
            recordPermissionState.launchPermissionRequest()
          }) {
            Text("Allow Record Permission")
          }
        } else {
          Text("Please enable Microphone permission in settings", textAlign = TextAlign.Center)
        }
      }
    }
  }
}