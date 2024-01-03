package dev.tberghuis.voicememos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dev.tberghuis.voicememos.screen.RecordingDetail

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    setContent {
      WearApp()
    }
  }
}


@Composable
fun WearApp() {
  val navController = rememberSwipeDismissableNavController()
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