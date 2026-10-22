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

// todo refactor so reusable with private recordings

@Composable
fun SharedRecordingCard(
  sharedRecording: SharedRecording,
  vm: MobileViewModel = viewModel()
) {
  val formattedTime = sharedRecording.displayName.toRecordingEntryTitle()

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
      Text("${sharedRecording.duration}s")
      Row {
        IconButton(onClick = {
          vm.sharedRecordingsVmc.playSharedRecording(sharedRecording)
        }) {
          Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "")
        }
        IconButton(onClick = {
          vm.sharedRecordingsVmc.deleteSharedRecording(sharedRecording)
        }) {
          Icon(imageVector = Icons.Default.Delete, contentDescription = "")
        }
      }
    }
  }
}

