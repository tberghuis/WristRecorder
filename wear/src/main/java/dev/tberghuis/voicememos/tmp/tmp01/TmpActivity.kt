package dev.tberghuis.voicememos.tmp.tmp01

import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.viewmodels.RecordingUiViewModel

class TmpActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)

//    val action: String? = intent?.action
    val data: Uri? = intent?.data
    logd("onCreate intent data ${data} ")

    setContent {
      val vm: RecordingUiViewModel = viewModel(
        viewModelStoreOwner
        = this
      )
      DisposableEffect(Unit) {
        val listener = Consumer<Intent> {
          logd("DisposableEffect listener intent $it")
          
          // todo if intent = wristrecorder://wristrecorder/toggle-recording
          
          vm.toggleRecording()
        }
        addOnNewIntentListener(listener)
        onDispose { removeOnNewIntentListener(listener) }
      }


      TmpApp()
    }
  }


  override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
    super.onNewIntent(intent, caller)
    logd("onNewIntent intent ${intent}")
  }
}

@Composable
fun TmpApp() {
  val navController = rememberSwipeDismissableNavController()
  MaterialTheme {
    SwipeDismissableNavHost(
      navController = navController, startDestination = "home"
    ) {
      composable("home") {
        Text("hello home")
      }
    }
  }
}