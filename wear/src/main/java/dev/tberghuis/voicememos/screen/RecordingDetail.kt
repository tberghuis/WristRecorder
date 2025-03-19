package dev.tberghuis.voicememos.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import dev.tberghuis.voicememos.MainActivity
import dev.tberghuis.voicememos.common.formatTimestampFromFilename
import dev.tberghuis.voicememos.common.logd
import kotlinx.coroutines.launch

@Composable
fun RecordingDetail(popBackStack: () -> Unit, popHomeRecording: () -> Unit) {
  val viewModel: RecordingDetailViewModel = viewModel()

  // do i need key??? probably, should write a test???
  val formattedTime = remember { formatTimestampFromFilename(viewModel.file) }

  val context = LocalContext.current
  LaunchedEffect(context) {
    (context as MainActivity).stemKeyUpSharedFlow.collect {
      popHomeRecording()
    }
  }

  Column(
    Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(formattedTime)
    Text("${viewModel.duration} seconds")
    Row(Modifier.padding(top = 10.dp)) {
      PlayButton()
      Spacer(modifier = Modifier.width(20.dp))
      DeleteButton()
    }
  }
  DeleteConfirmAlert(popBackStack)
}

@Composable
fun DeleteConfirmAlert(popBackStack: () -> Unit) {
  val viewModel: RecordingDetailViewModel = viewModel()
  if (viewModel.showDeleteConfirm.value) {
    Alert(title = {
      Text("Confirm Delete?")
    }, positiveButton = {
      Button(onClick = {
        logd("pos")
        viewModel.deleteFileService.deleteFile(viewModel.file)
        popBackStack()
      }) {
        Icon(
          imageVector = Icons.Filled.Check,
          contentDescription = "OK",
          modifier = Modifier.size(30.dp),
        )
      }
    }, negativeButton = {
      Button(onClick = {
        logd("neg")
        viewModel.showDeleteConfirm.value = false
      }) {
        Icon(
          imageVector = Icons.Filled.Close,
          contentDescription = "Cancel",
          modifier = Modifier.size(30.dp),
        )
      }
    })
  }
}

@Composable
fun PlayButton() {
  val viewModel: RecordingDetailViewModel = viewModel()
  val scope = rememberCoroutineScope()

  Button(onClick = {
    logd("play")
    scope.launch {
      viewModel.audioController.play(viewModel.file)
    }
  }) {
    Icon(
      imageVector = Icons.Filled.PlayArrow,
      contentDescription = "play",
      modifier = Modifier.size(60.dp)
    )
  }
}

@Composable
fun DeleteButton() {
  val viewModel: RecordingDetailViewModel = viewModel()
  Button(onClick = {
    logd("delete")
    viewModel.showDeleteConfirm.value = true
  }) {
    Icon(
      imageVector = Icons.Filled.Delete,
      contentDescription = "delete",
      modifier = Modifier.size(60.dp)
    )
  }
}