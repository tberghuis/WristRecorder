package dev.tberghuis.voicememos.tmp.tmp01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dev.tberghuis.voicememos.HomeScreen
import dev.tberghuis.voicememos.screen.RecordingDetail

class TmpActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    setContent {
      TmpApp()
    }
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