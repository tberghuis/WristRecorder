package dev.tberghuis.voicememos.tmp2

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Icon
import dev.tberghuis.voicememos.HomeViewModel
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.composables.isHardwareButtonPress
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun TmpRecordingUi(
  navigateRecordingDetail: (String) -> Unit,
  permissionPrompt: (() -> Unit)? = null
) {
  val scope = rememberCoroutineScope()
  val viewModel: HomeViewModel = viewModel()
  val context = LocalContext.current
  val recordingJob = remember { mutableStateOf<Job?>(null) }
  var filename by remember { mutableStateOf<String?>(null) }
  val requester = remember { FocusRequester() }
  LaunchedEffect(Unit) {
    requester.requestFocus()
  }

  val record = fun() {
    logd("record")
    permissionPrompt?.let {
      it.invoke()
      return
    }
//    if (ActivityCompat.checkSelfPermission(
//        context, Manifest.permission.RECORD_AUDIO
//      ) != PackageManager.PERMISSION_GRANTED
//    ) {
//      return
//    }
    recordingJob.value = scope.launch {
      viewModel.audioController.record { filename = it }
    }
    logd("after recordingJob launch")
  }

  val endRecord = {
    logd("end record")
    recordingJob.value?.cancel()
    recordingJob.value = null
    filename?.let {
      navigateRecordingDetail(it)
    }
  }


  val modifier = when (recordingJob.value) {
    null -> {
      Modifier
        .clickable {
          record()
        }
        .onKeyEvent { keyEvent ->
          logd("keyEvent $keyEvent")
          if (isHardwareButtonPress(keyEvent)) {
            record()
            return@onKeyEvent true
          }
          false
        }
    }

    else -> {
      Modifier
        .clickable {
          endRecord()
        }
        .onKeyEvent { keyEvent ->
          logd("keyEvent $keyEvent")
          if (isHardwareButtonPress(keyEvent)) {
            endRecord()
            return@onKeyEvent true
          }
          false
        }
    }
  }

  Box(
    modifier
      .focusRequester(requester)
      .focusable(),
  ) {
    if (recordingJob.value == null) {
      Icon(
        imageVector = Icons.Filled.Circle,
        contentDescription = "start recording",
        tint = Color.Red,
        modifier = Modifier.size(80.dp)
      )
    } else {
      Icon(
        imageVector = Icons.Filled.Stop,
        contentDescription = "stop recording",
        tint = Color.White,
        modifier = Modifier.size(80.dp)
      )
    }
  }
}
