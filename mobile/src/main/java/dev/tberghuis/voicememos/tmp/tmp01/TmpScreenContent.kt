package dev.tberghuis.voicememos.tmp.tmp01

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.MobileViewModel
import dev.tberghuis.voicememos.RecordingCard

@Composable
fun TmpScreenContent(
  padding: PaddingValues, vm: MobileViewModel = viewModel()
) {
  val files = vm.recordingFilesStateFlow.collectAsState()
  LazyColumn(
    modifier = Modifier
      .padding(padding)
      .fillMaxSize(),
  ) {
    item {
      Text("hello screen content")
    }
    items(files.value.size) { i ->
      RecordingCard(files.value[i])
    }
  }
}