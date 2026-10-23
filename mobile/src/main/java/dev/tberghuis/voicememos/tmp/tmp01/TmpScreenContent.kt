package dev.tberghuis.voicememos.tmp.tmp01

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.RecordingCard

@Composable
fun TmpScreenContent(
  padding: PaddingValues, vm: MobileViewModel = viewModel()
) {
  val files = vm.recordingFilesStateFlow.collectAsState()
  val sharedRecordingsList by vm.sharedRecordingsVmc.sharedRecordingsList.collectAsState()

  LazyColumn(
    modifier = Modifier
      .padding(padding)
      .fillMaxSize(),
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Private storage: ")
        Text(
          text = "move to shared",
          style = LocalTextStyle.current.copy(
            color = Color(0xFF1A73E8), // Standard hyperlink blue
            textDecoration = TextDecoration.Underline
          ),
          modifier = Modifier
            .clickable {
              vm.moveRecordingsVmc.moveToSharedStorage()
            }
        )
      }
    }

    if (files.value.isEmpty()) {
      item {
        NoRecordingsCard()
      }
    } else {
      items(
        files.value.size,
        key = {
          files.value[it].name
        }
      ) { i ->
        RecordingCard(files.value[i])
      }
    }

    item {
  
      Spacer(Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Shared Storage: /$WR_RELATIVE_PATH")
      }
    }
    items(
      sharedRecordingsList.size,
      key = {
        sharedRecordingsList[it].id
      }
    ) { i ->
      SharedRecordingCard(sharedRecordingsList[i])
    }
  }
}


@Composable
fun NoRecordingsCard() {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(5.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.Top,
    ) {
      Text(
        "no recordings",
        fontStyle = FontStyle.Italic
      )
    }
  }

}