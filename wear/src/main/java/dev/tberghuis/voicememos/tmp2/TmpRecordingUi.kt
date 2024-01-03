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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Icon
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.composables.isHardwareButtonPress

@SuppressLint("MissingPermission")
@Composable
fun TmpRecordingUi(
  navigateRecordingDetail: (String) -> Unit,
  vm: TmpRecordingUiViewModel = viewModel(),
  permissionPrompt: (() -> Unit)? = null
) {
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
    vm.startRecording()
//    recordingJob.value = scope.launch {
//      vm.audioController.record { filename = it }
//    }
  }


//  val endRecord = {
//    logd("end record")
//    recordingJob.value?.cancel()
//    recordingJob.value = null
//    filename?.let {
//      navigateRecordingDetail(it)
//    }
//  }

  val endRecord = {
    vm.stopRecording()?.let {
      navigateRecordingDetail(it)
    }
  }

  val modifier = if (vm.isRecording) {
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
  } else {
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

  Box(
    modifier
      .focusRequester(requester)
      .focusable(),
  ) {
    if (vm.isRecording) {
      Icon(
        imageVector = Icons.Filled.Stop,
        contentDescription = "stop recording",
        tint = Color.White,
        modifier = Modifier.size(80.dp)
      )
    } else {
      Icon(
        imageVector = Icons.Filled.Circle,
        contentDescription = "start recording",
        tint = Color.Red,
        modifier = Modifier.size(80.dp)
      )
    }
  }
}