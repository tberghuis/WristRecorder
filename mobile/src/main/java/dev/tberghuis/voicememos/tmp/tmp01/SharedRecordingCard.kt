package dev.tberghuis.voicememos.tmp.tmp01

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.common.calcDuration
import dev.tberghuis.voicememos.common.formatTimestampFromFilename
import java.io.File

// todo refactor so reusable with private recordings

@Composable
fun SharedRecordingCard(
  sharedRecording: SharedRecording
) {
  val context = LocalContext.current

//  val formattedTime = formatTimestampFromFilename(recordingFile.name)
//  val durationSeconds = calcDuration(context, recordingFile.name)

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
//      Text(formattedTime)
//      Text("${durationSeconds}s")
      Text(sharedRecording.displayName)
      Row {
        IconButton(onClick = {
//          vm.playRecording(recordingFile)
        }) {
          Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "")
        }
        IconButton(onClick = {
//          vm.deleteRecording(recordingFile)
        }) {
          Icon(imageVector = Icons.Default.Delete, contentDescription = "")
        }
      }
    }
  }
}

