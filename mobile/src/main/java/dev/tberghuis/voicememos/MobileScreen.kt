package dev.tberghuis.voicememos

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileScreen(
  vm: MobileViewModel = viewModel()
) {
  val context = LocalContext.current
  Scaffold(
    bottomBar = {
      BottomAppBar() {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          Button(
            modifier = Modifier,
            onClick = { vm.syncRecordings(context as Activity) }) {
            Text("Download recordings and delete from watch")
          }
        }
      }
    }
  ) { paddingValues ->
    ScreenContent(paddingValues)
  }
}

@Composable
fun ScreenContent(
  padding: PaddingValues,
  vm: MobileViewModel = viewModel()
) {

  val files = vm.recordingFilesStateFlow.collectAsState()

  LazyColumn(
    modifier = Modifier
      .padding(padding)
      .fillMaxSize(),
  ) {

    items(files.value.size) { i ->
      Text("filename: ${files.value[i].name}")
    }
  }
}
