package dev.tberghuis.voicememos.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import dev.tberghuis.voicememos.HomeViewModel
import dev.tberghuis.voicememos.common.calcDuration
import dev.tberghuis.voicememos.common.formatTimestampFromFilename
import dev.tberghuis.voicememos.common.logd

@Composable
fun RecordingList(
  onRecordingClick: (String) -> Unit
) {
  val viewModel: HomeViewModel = hiltViewModel()
  val context = LocalContext.current

  if (viewModel.recordingFilesInitialised.value && viewModel.recordingFiles.value.isEmpty()) {
    Column(
      Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text("No recordings")
    }
    return
  }

  ScalingLazyColumn {
    items(viewModel.recordingFiles.value.size) { i ->
      val file = viewModel.recordingFiles.value[i]
      val buttonText = remember(file, context) {
        "${formatTimestampFromFilename(file)} ${calcDuration(context, file)}s"
      }
      Button(modifier = Modifier.fillMaxWidth(),
        onClick = {
          logd("play $file")
          onRecordingClick(file)
        }
      ) {
        Text(buttonText, Modifier.padding(horizontal = 10.dp))
      }
    }
  }
}

