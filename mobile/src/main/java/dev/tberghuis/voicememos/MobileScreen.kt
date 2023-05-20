package dev.tberghuis.voicememos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.common.calcDuration
import dev.tberghuis.voicememos.common.formatTimestampFromFilename
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileScreen(
  vm: MobileViewModel = viewModel()
) {
  Scaffold(topBar = {
    TopAppBar(title = { Text(stringResource(R.string.app_name)) })
  }, bottomBar = {
    BottomAppBar {
      Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
      ) {
        Button(modifier = Modifier, onClick = { vm.downloadRecordings() }) {
          Text("Download")
        }
        Button(modifier = Modifier.padding(start = 10.dp), onClick = { vm.deleteAllWatch() }) {
          Text("Delete All Watch")
        }
      }
    }
  }) { paddingValues ->
    ScreenContent(paddingValues)
  }
}

@Composable
fun ScreenContent(
  padding: PaddingValues, vm: MobileViewModel = viewModel()
) {
  val files = vm.recordingFilesStateFlow.collectAsState()
  LazyColumn(
    modifier = Modifier
      .padding(padding)
      .fillMaxSize(),
  ) {
    items(files.value.size) { i ->
      RecordingCard(files.value[i])
    }
  }
}

@Composable
fun RecordingCard(
  recordingFile: File, vm: MobileViewModel = viewModel()
) {
  val context = LocalContext.current
  // doitwrong
  // wrap in remember???
  val formattedTime = formatTimestampFromFilename(recordingFile.name)
  val durationSeconds = calcDuration(context, recordingFile.name)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(5.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(formattedTime)
      Text("${durationSeconds}s")
      Row {
        IconButton(onClick = {
          vm.playRecording(recordingFile)
        }) {
          Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "")
        }
        IconButton(onClick = {
          vm.deleteRecording(recordingFile)
        }) {
          Icon(imageVector = Icons.Default.Delete, contentDescription = "")
        }
      }
    }
  }
}