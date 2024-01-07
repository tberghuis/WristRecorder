package dev.tberghuis.voicememos.composables

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Icon
import dev.tberghuis.voicememos.MainActivity
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.viewmodels.RecordingUiViewModel
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
@Composable
fun RecordingUi(
  navigateRecordingDetail: (String) -> Unit,
  vm: RecordingUiViewModel = viewModel(),
  permissionPrompt: (() -> Unit)? = null
) {
//  val requester = remember { FocusRequester() }
//  LaunchedEffect(Unit) {
//    logd("RecordingUi LaunchedEffect before delay")
//    // should i create a bug or just live with it
//    // meh
//    delay(500)
//    logd("RecordingUi LaunchedEffect after delay")
//    requester.requestFocus()
//  }


  val record = fun() {
    logd("record")
    permissionPrompt?.let {
      it.invoke()
      return
    }
    vm.startRecording()
  }

  val endRecord = {
    vm.stopRecording()?.let {
      navigateRecordingDetail(it)
    }
  }

  val context = LocalContext.current


  LaunchedEffect(context) {
    logd("RecordingUi LaunchedEffect")
    (context as MainActivity).stemKeyUpSharedFlow.collect {
      logd("stemKeyUpSharedFlow collect")
      when (vm.isRecording) {
        true -> endRecord()
        false -> record()
      }
    }
  }


  val modifier = if (vm.isRecording) {
    Modifier
      .clickable {
        endRecord()
      }
//      .onKeyEvent { keyEvent ->
//        logd("keyEvent $keyEvent")
//        if (isHardwareButtonPress(keyEvent)) {
//          endRecord()
//          return@onKeyEvent true
//        }
//        false
//      }
  } else {
    Modifier
      .clickable {
        record()
      }
//      .onKeyEvent { keyEvent ->
//        logd("keyEvent $keyEvent")
//        if (isHardwareButtonPress(keyEvent)) {
//          record()
//          return@onKeyEvent true
//        }
//        false
//      }
  }

  Box(
    modifier
//      .focusRequester(requester)
//      .focusable(),
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