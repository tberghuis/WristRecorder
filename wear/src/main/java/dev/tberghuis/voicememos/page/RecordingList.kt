package dev.tberghuis.voicememos.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import dev.tberghuis.voicememos.HomeViewModel
import dev.tberghuis.voicememos.presentation.calcDuration
import dev.tberghuis.voicememos.util.logd
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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
      Button(modifier = Modifier,
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

fun formatTimestampFromFilename(file: String): String {
  // should learn regex
  val timestamp = file.split("_")[1].split(".")[0].toLong()
  val date = LocalDateTime.ofInstant(
    Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()
  )
//  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  val formatter = DateTimeFormatter.ofPattern("EEE, d MMM HH:mm")
  return date.format(formatter)
}