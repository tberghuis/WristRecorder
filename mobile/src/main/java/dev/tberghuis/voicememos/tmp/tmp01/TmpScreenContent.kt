package dev.tberghuis.voicememos.tmp.tmp01

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.RecordingCard
import dev.tberghuis.voicememos.common.logd

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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Private storage:")
        Button(onClick = {
          logd("move to shared")

          vm.moveRecordingsVmc.moveToSharedStorage()

        }) {
          Text("move to shared")
        }
      }

    }
    items(files.value.size) { i ->
      RecordingCard(files.value[i])
    }




    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Shared Storage: /$WR_RELATIVE_PATH")
      }
    }

    // todo remove
    item {
      Button(onClick = {
        vm.sharedRecordingsVmc.refreshSharedList()
      }) {
        Text("refresh shared")
      }
    }

    items(sharedRecordingsList.size) { i ->
      SharedRecordingCard(sharedRecordingsList[i])
    }
  }
}