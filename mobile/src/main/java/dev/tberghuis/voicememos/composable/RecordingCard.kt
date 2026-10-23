package dev.tberghuis.voicememos.composable

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.tberghuis.voicememos.util.getFormattedTimestamp

@Composable
fun RecordingCard(
  filename: String,
  duration: Int,
  play: () -> Unit,
  delete: () -> Unit,
) {
  val formattedTime = getFormattedTimestamp(filename)
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
      if (formattedTime != null) {
        Text(formattedTime)
        Text("${duration}s")
      } else {
        Text(
          filename.substringBeforeLast("."),
          modifier = Modifier.weight(1f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      Row {
        IconButton(onClick = {
          play()
        }) {
          Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "")
        }
        IconButton(onClick = {
          delete()
        }) {
          Icon(imageVector = Icons.Default.Delete, contentDescription = "")
        }
      }
    }
  }
}