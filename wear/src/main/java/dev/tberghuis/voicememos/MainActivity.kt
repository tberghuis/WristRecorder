package dev.tberghuis.voicememos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dev.tberghuis.voicememos.common.logd
import dev.tberghuis.voicememos.screen.RecordingDetail
import dev.tberghuis.voicememos.viewmodels.RecordingUiViewModel
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
//  val backButtonPressed = mutableStateOf(false)
  var backButtonPressed = false
  var resetBackButtonPressedJob: Job? = null

  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    logd("MainActivity onKeyDown keyCode $keyCode event $event")
    if (keyCode == KEYCODE_BACK) {
      resetBackButtonPressedJob?.cancel()
//      backButtonPressed.value = true
      backButtonPressed = true
    }
    return super.onKeyUp(keyCode, event)
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
    logd("MainActivity onKeyUp keyCode $keyCode event $event")
    if (keyCode == KEYCODE_BACK) {
      resetBackButtonPressedJob = lifecycleScope.launch {
        delay(1000.milliseconds)
//        backButtonPressed.value = false
        backButtonPressed = false
      }
    }
    return super.onKeyUp(keyCode, event)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    var data: Uri? = intent?.data
    logd("onCreate intent data $data")
    setContent {
      val vm: RecordingUiViewModel = viewModel(
        viewModelStoreOwner
        = this
      )
      if (data.toString() == "wristrecorder://wristrecorder/toggle-recording") {
        LaunchedEffect(Unit) {
          // only run once even on configuration change
          data = null
          vm.toggleRecording()
        }
      }
      DisposableEffect(Unit) {
        val listener = Consumer<Intent> {
          logd("DisposableEffect listener intent $it")
          if (it.data.toString() == "wristrecorder://wristrecorder/toggle-recording") {
            vm.toggleRecording()
          }
        }
        addOnNewIntentListener(listener)
        onDispose { removeOnNewIntentListener(listener) }
      }
      WearApp()
    }
  }
}

@Composable
fun WearApp() {
  val navController = rememberSwipeDismissableNavController()

  CompositionLocalProvider(LocalNavController provides navController) {
    MaterialTheme {
      SwipeDismissableNavHost(
        navController = navController, startDestination = "home"
      ) {
        composable("home") {
          HomeScreen(navigateRecordingDetail = { file ->
            navController.navigate("recording_detail/$file")
          })
        }
        composable("recording_detail/{file}") {
          RecordingDetail(popBackStack = {
            navController.popBackStack()
          }, popHomeRecording = {
            // https://stackoverflow.com/questions/75131781/how-to-refresh-the-content-of-the-fragments-with-the-navigation-component-when-r
            navController.popBackStack(
              route = "home",
              inclusive = true,
            )
            navController.navigate("home")
          })
        }
      }
    }
  }
}