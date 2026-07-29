package dev.tberghuis.voicememos.composables

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Icon
import dev.tberghuis.voicememos.HomeViewModel
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.viewmodels.RecordingUiViewModel

@SuppressLint("MissingPermission")
@Composable
fun RecordingUi(
  navigateRecordingDetail: (String) -> Unit,
  vm: RecordingUiViewModel = viewModel(
    viewModelStoreOwner
    = LocalActivity.current as ViewModelStoreOwner
  ),
  permissionPrompt: (() -> Unit)? = null
) {
  
  val homeVm: HomeViewModel = viewModel()
  if (homeVm.pagerState.currentPage == 0) {
    BackButtonOverride {
      permissionPrompt?.let {
        it()
        return@BackButtonOverride
      }
      vm.toggleRecording()
    }
  }

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

  val isRecording = vm.isRecording ?: return
  Box(
    Modifier
      .clickable {
        when (isRecording) {
          true -> endRecord()
          false -> record()
        }
      }
  ) {
    when (isRecording) {
      true -> {
        Icon(
          imageVector = Icons.Filled.Stop,
          contentDescription = "stop recording",
          tint = Color.White,
          modifier = Modifier.size(80.dp)
        )
      }

      false -> {
        Icon(
          imageVector = Icons.Filled.Circle,
          contentDescription = "start recording",
          tint = Color.Red,
          modifier = Modifier.size(80.dp)
        )
      }
    }
  }
}