package dev.tberghuis.voicememos

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MobileScreen(
  vm: MobileViewModel = viewModel()
) {
  val context = LocalContext.current

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("hello mobile screen ${vm.willitblend}")
    Button(onClick = {
      vm.syncRecordings(context as Activity)
    }) {
      Text("send message")
    }
  }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldDemo() {
  Scaffold(
    bottomBar = {
      BottomAppBar() {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          Button(
            modifier = Modifier,
            onClick = {}) {
            Text("Download recordings and delete from watch")
          }
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier.padding(paddingValues),
    ) {
      Text("hello preview")
    }
  }
}