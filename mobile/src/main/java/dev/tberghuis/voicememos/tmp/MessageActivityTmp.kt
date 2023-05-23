package dev.tberghuis.voicememos.tmp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tberghuis.voicememos.ui.theme.FreshMobileTheme


class MessageActivityTmp : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      FreshMobileTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          TmpScreen()
        }
      }
    }
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TmpScreen(
  vm: MessageViewModelTmp = viewModel()
) {

  Scaffold(
    snackbarHost = {
      SnackbarHost(vm.snackbarHostState) { data ->
        Snackbar() {
          Text(data.visuals.message)
        }
      }
    }
  ) {
    TmpScreenContent(it)
  }
}


@Composable
fun TmpScreenContent(
  paddingValues: PaddingValues,
  vm: MessageViewModelTmp = viewModel()

) {
  Column(Modifier.padding(paddingValues)) {
    Text("hello tmp screen")
    Button(onClick = {
      vm.willitblend()
    }) {
      Text("willitblend")
    }
  }

}