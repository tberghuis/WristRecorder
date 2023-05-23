package dev.tberghuis.voicememos.tmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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

@Composable
fun TmpScreen(
  vm: MessageViewModelTmp = viewModel()

) {
  Column {
    Text("hello tmp screen")
    Button(onClick = {
      vm.willitblend()
    }) {
      Text("willitblend")
    }
  }
}